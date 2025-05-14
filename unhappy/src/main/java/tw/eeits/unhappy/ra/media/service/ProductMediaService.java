package tw.eeits.unhappy.ra.media.service;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import tw.eeits.unhappy.eeit198product.entity.Product;
import tw.eeits.unhappy.ra.media.model.MediaType;
import tw.eeits.unhappy.ra.media.model.ProductMedia;
import tw.eeits.unhappy.ra.media.repository.ProductMediaRepository;
import tw.eeits.unhappy.ra.storage.StorageService;
import tw.eeits.unhappy.ra.storage.StorageException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductMediaService {

    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5 MB 上限

    private final ProductMediaRepository repo;
    private final StorageService storage;

    /** 上傳並建立一筆 media；若 main=true 會自動取消舊主圖 */
    @Transactional
    public ProductMedia upload(Integer productId,
                               MultipartFile file,
                               boolean main,
                               Integer order) throws IOException {

        log.info("Attempting to upload media for product ID: {}", productId);

        // 0. 檔案大小和類型驗證
        if (file.getSize() > MAX_SIZE) {
            log.warn("File size exceeds limit for product ID {}. Size: {}", productId, file.getSize());
            throw new IllegalArgumentException("檔案大小不得超過 " + MAX_SIZE / 1024 / 1024 + " MB");
        }

        // 【修正】根據檔案 Content Type 判斷媒體類型
        String contentType = file.getContentType();
        MediaType detectedMediaType;
        if (contentType == null) {
            log.warn("Uploaded file has no content type for product ID {}: {}", productId, file.getOriginalFilename());
            throw new IllegalArgumentException("無法識別檔案類型");
        } else if (contentType.startsWith("image/")) {
            detectedMediaType = MediaType.IMAGE;
            log.debug("Detected media type: IMAGE for file: {}", file.getOriginalFilename());
        } else if (contentType.startsWith("video/")) {
            detectedMediaType = MediaType.VIDEO;
            log.debug("Detected media type: VIDEO for file: {}", file.getOriginalFilename());
        } else {
            log.warn("Unsupported file type uploaded for product ID {}: {}", productId, contentType);
            throw new IllegalArgumentException("不支援的檔案類型: " + contentType);
        }


        // 1. 儲存到雲端
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        // 使用原始副檔名，雲端儲存通常會根據 ContentType 正確處理
        String path = "product-media/%d/%s%s".formatted(productId, UUID.randomUUID(), (ext != null && !ext.isEmpty() ? "." + ext : ""));
        String mediaUrl;
        try {
            log.info("Uploading file to storage: {}", path);
            // 【修正】使用偵測到的 Content Type 進行上傳
            mediaUrl = storage.upload(
                    path,
                    file.getInputStream(),
                    file.getSize(),
                    contentType); // 使用實際的 Content Type
            log.info("File uploaded successfully: {}", mediaUrl);
        } catch (StorageException e) {
            log.error("Failed to upload file to storage: {}", path, e);
            throw new RuntimeException("檔案上傳至雲端失敗", e);
        } catch (IOException e) {
            log.error("Failed to read file input stream for upload: {}", path, e);
            throw new RuntimeException("讀取檔案失敗", e);
        } catch (Exception e) {
            log.error("Unexpected error during file upload: {}", path, e);
            throw new RuntimeException("檔案上傳時發生未知錯誤", e);
        }

        // 2. 處理 mediaOrder
        // 如果沒有指定 order，則設定為目前 media 數量的下一號
        Integer finalOrder = (order != null) ? order : (int) repo.countByProductId(productId) + 1;
        log.debug("Final media order for new media: {}", finalOrder);

        // 3. 建立 ProductMedia 記錄
        ProductMedia media = new ProductMedia();
        Product product = new Product();
        product.setId(productId);
        media.setProduct(product);

        // 【修正】設定偵測到的媒體類型
        media.setMediaType(detectedMediaType);
        media.setMediaUrl(mediaUrl);
        media.setAltText(FilenameUtils.getBaseName(file.getOriginalFilename())); // 使用檔名作為 Alt Text
        media.setMediaOrder(finalOrder);
        media.setIsMain(main);

        // 4. 處理主圖設定
        if (main) {
            log.info("Setting new media ID {} as main for product ID {}", media.getId(), productId);
            // 取消同商品下的舊主圖
            repo.findFirstByProductIdAndIsMainTrue(productId).ifPresent(oldMain -> {
                log.info("Unsetting old main media ID {} for product ID {}", oldMain.getId(), productId);
                oldMain.setIsMain(false);
                repo.save(oldMain);
            });
        }

        // 5. 保存新的 ProductMedia 記錄
        try {
            log.info("Saving new media record for product ID {}", productId);
            ProductMedia savedMedia = repo.save(media);
            log.info("New media record saved successfully. ID: {}", savedMedia.getId());

            // 如果設定為主圖，需要重新排序，將其移到第一位
            if (main) {
                reorderMediaAfterMainSet(productId, savedMedia.getId());
            }
            // 否則，如果不是主圖，則無需額外重新排序，其 order 在建立時已設定

            return savedMedia;
        } catch (DataAccessException e) {
            log.error("Failed to save media record to DB for product ID {}", productId, e);
            // 如果資料庫保存失敗，可能需要考慮刪除已經上傳到雲端的檔案
            // storage.delete(path); // 這裡需要完整的 path
            throw new RuntimeException("保存媒體記錄失敗", e);
        } catch (Exception e) {
            log.error("Unexpected error during saving media record for product ID {}", productId, e);
            throw new RuntimeException("保存媒體記錄時發生未知錯誤", e);
        }
    }

    /** 批量上傳媒體 */
    @Transactional
    public List<ProductMedia> uploadBatch(Integer productId, List<MultipartFile> files) throws IOException {
        log.info("Attempting to upload batch media for product ID: {}. File count: {}", productId, files.size());
        List<ProductMedia> savedMediaList = new ArrayList<>();

        // 獲取當前媒體的最大 order，用於新上傳圖片的起始 order
        long currentMediaCount = repo.countByProductId(productId);
        int startOrder = (int) currentMediaCount; // 從當前數量開始編號 (0-based index)
        log.debug("Starting order for batch upload: {}", startOrder);

        List<ProductMedia> newlyUploadedMedia = new ArrayList<>(); // 儲存本次新上傳的媒體

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);

            // 0. 檔案大小和類型驗證
            if (file.getSize() > MAX_SIZE) {
                log.warn("File size exceeds limit during batch upload for product ID {}. File: {}, Size: {}",
                        productId, file.getOriginalFilename(), file.getSize());
                log.warn("Skipping file {} due to size limit.", file.getOriginalFilename());
                continue; // 跳過此檔案
            }

            // 【修正】根據檔案 Content Type 判斷媒體類型
            String contentType = file.getContentType();
            MediaType detectedMediaType = null;
            if (contentType == null) {
                log.warn("Batch uploaded file has no content type for product ID {}: {}. Skipping.",
                        productId, file.getOriginalFilename());
                log.warn("Skipping file {} due to unidentified type.", file.getOriginalFilename());
                continue; // 跳過此檔案
            } else if (contentType.startsWith("image/")) {
                detectedMediaType = MediaType.IMAGE;
                log.debug("Detected media type: IMAGE for batch file: {}", file.getOriginalFilename());
            } else if (contentType.startsWith("video/")) {
                detectedMediaType = MediaType.VIDEO;
                log.debug("Detected media type: VIDEO for batch file: {}", file.getOriginalFilename());
            } else {
                log.warn("Unsupported file type detected during batch upload for product ID {}: {}. Skipping.",
                        productId, contentType);
                log.warn("Skipping file {} due to unsupported type.", file.getOriginalFilename());
                continue; // 跳過此檔案
            }

            // 1. 儲存到雲端
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            // 使用原始副檔名
            String path = "product-media/%d/%s%s".formatted(productId, UUID.randomUUID(), (ext != null && !ext.isEmpty() ? "." + ext : ""));
            String mediaUrl;
            try {
                log.info("Uploading batch file to storage: {}", path);
                // 【修正】使用偵測到的 Content Type 進行上傳
                mediaUrl = storage.upload(
                        path,
                        file.getInputStream(),
                        file.getSize(),
                        contentType); // 使用實際的 Content Type
                log.info("Batch file uploaded successfully: {}", mediaUrl);
            } catch (StorageException e) {
                log.error("Failed to upload batch file to storage: {}", path, e);
                log.error("Skipping file {} due to storage upload failure.", file.getOriginalFilename());
                continue; // 跳過此檔案
            } catch (IOException e) {
                log.error("Failed to read batch file input stream for upload: {}", path, e);
                log.error("Skipping file {} due to read failure.", file.getOriginalFilename());
                continue; // 跳過此檔案
            } catch (Exception e) {
                log.error("Unexpected error during batch file upload: {}", path, e);
                log.error("Skipping file {} due to unexpected error.", file.getOriginalFilename());
                continue; // 跳過此檔案
            }

            // 2. 建立 ProductMedia 記錄
            ProductMedia media = new ProductMedia();
            Product product = new Product();
            product.setId(productId);
            media.setProduct(product);

            // 【修正】設定偵測到的媒體類型
            media.setMediaType(detectedMediaType);
            media.setMediaUrl(mediaUrl);
            media.setAltText(FilenameUtils.getBaseName(file.getOriginalFilename())); // 使用檔名作為 Alt Text
            media.setMediaOrder(startOrder + i); // 按照上傳順序設定 order
            media.setIsMain(false); // 批量上傳的媒體預設不是主圖

            // 3. 保存新的 ProductMedia 記錄
            try {
                log.info("Saving new media record for batch upload for product ID {}", productId);
                ProductMedia savedMedia = repo.save(media);
                log.info("New media record saved successfully. ID: {}", savedMedia.getId());
                savedMediaList.add(savedMedia);
                newlyUploadedMedia.add(savedMedia); // 將新保存的媒體添加到新上傳列表中
            } catch (DataAccessException e) {
                log.error("Failed to save media record to DB for product ID {} during batch upload.", productId, e);
                // 如果資料庫保存失敗，可能需要考慮刪除已經上傳到雲端的檔案
                // storage.delete(path); // 這裡需要完整的 path
                log.error("Skipping file {} due to DB save failure.", file.getOriginalFilename());
            } catch (Exception e) {
                log.error("Unexpected error during saving media record for product ID {} during batch upload.",
                        productId, e);
                log.error("Skipping file {} due to unexpected DB error.", file.getOriginalFilename());
            }
        }

        // 批量上傳完成後，檢查並設定主圖
        if (!newlyUploadedMedia.isEmpty()) {
            log.info("Batch upload finished, checking for main image for product ID {}", productId);
            // 檢查商品是否已經有主圖
            Optional<ProductMedia> existingMain = repo.findFirstByProductIdAndIsMainTrue(productId);

            if (existingMain.isEmpty()) {
                log.info("No existing main image found for product ID {}. Setting the first newly uploaded media as main.",
                        productId);
                // 如果沒有主圖，將本次上傳的第一個媒體設為主圖
                ProductMedia firstUploaded = newlyUploadedMedia.get(0);
                firstUploaded.setIsMain(true);
                try {
                    repo.save(firstUploaded);
                    log.info("Set media ID {} as main after batch upload.", firstUploaded.getId());
                    // 設定主圖後，觸發重新排序，將其移到第一位
                    reorderMediaAfterMainSet(productId, firstUploaded.getId());
                } catch (DataAccessException e) {
                    log.error("Failed to save updated media record (set as main) to DB for ID {}",
                            firstUploaded.getId(), e);
                    // 這裡的失敗比較關鍵，可能需要向前端報告
                } catch (Exception e) {
                    log.error("Unexpected error saving updated media record (set as main) to DB for ID {}",
                            firstUploaded.getId(), e);
                }
            } else {
                log.info("Existing main image found for product ID {}. No new main media set automatically.",
                        productId);
                // 如果有主圖，不需要自動設定新的主圖
                // 由於 uploadBatch 已經按順序賦予 order，這裡通常不需要額外觸發 reorder
            }
        } else {
            log.warn("No media were successfully uploaded in the batch for product ID {}", productId);
        }

        log.info("Finished batch uploading media for product ID {}. Successfully saved {} records.", productId,
                savedMediaList.size());
        return savedMediaList; // 返回成功保存的媒體列表
    }

    /** 拖曳後重新排序 media */
    @Transactional
    public void reorder(Integer productId, Map<Integer, Integer> newOrders) {
        log.info("Attempting to reorder media for product ID {}. New orders: {}", productId, newOrders);
        // 獲取該商品的所有 media，並鎖行
        List<ProductMedia> mediaList = repo.findByProductId(productId);
        log.debug("Found {} media records for product ID {} for reordering.", mediaList.size(), productId);

        // 根據傳入的 newOrders 更新 mediaOrder
        mediaList.forEach(m -> {
            Integer newOrder = newOrders.get(m.getId());
            if (newOrder != null) {
                m.setMediaOrder(newOrder);
                log.debug("Updated media ID {} order to {}", m.getId(), newOrder);
            }
        });

        // 確保主圖的 order 是 1
        Optional<ProductMedia> mainMedia = mediaList.stream().filter(ProductMedia::getIsMain).findFirst();
        if (mainMedia.isPresent()) {
            ProductMedia main = mainMedia.get();
            // 如果主圖的 order 不是 1，則調整所有媒體的 order
            if (main.getMediaOrder() != 1) {
                log.info("Main media ID {} is not order 1, adjusting orders.", main.getId());
                // 將所有 order 大於等於 1 的圖片 order + 1
                // 注意：這裡應該根據新的排序結果來調整 order，而不是簡單地 +1
                // 更安全的做法是按照新的順序重新編號
                reorderMediaAfterMainSet(productId, main.getId()); // 呼叫已有的重新排序邏輯
                return; // 重新排序邏輯會保存，這裡直接返回
            }
        } else {
            log.warn("No main media found for product ID {} after reorder. Recommend setting one.", productId);
            // 如果沒有主圖，可能需要考慮將第一張圖片設為主圖（可選）
            // 這裡我們選擇不自動設定主圖，讓使用者手動設定
        }

        // 如果主圖已經是 order 1，或者沒有主圖，只需要保存更新了 order 的 media
        try {
            log.info("Saving reordered media for product ID {} (after processing main). Count: {}", productId, mediaList.size());
            repo.saveAll(mediaList);
            log.info("Successfully saved reordered media for product ID {}", productId);
        } catch (DataAccessException e) {
            log.error("Failed to save reordered media to DB for product ID {}", productId, e);
            throw new RuntimeException("保存媒體排序失敗", e);
        } catch (Exception e) {
            log.error("Unexpected error saving reordered media for product ID {}", productId, e);
            throw new RuntimeException("保存媒體排序時發生未知錯誤", e);
        }

        log.info("Finished reordering media for product ID: {}", productId);
    }


    /** 刪除 media 並移除雲端檔案，再重新編號 */
    @Transactional
    public void delete(Integer id) {
        log.info("Attempting to delete single media ID: {}", id);
        ProductMedia m = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Media not found for single deletion. ID: {}", id);
                    return new IllegalArgumentException("Media not found: " + id);
                });
        log.info("Found media for deletion: ID={}, Product ID={}", id, m.getProduct().getId());

        // 刪除雲端檔案
        try {
            URI uri = URI.create(m.getMediaUrl());
            String path = uri.getPath();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            log.info("Deleting cloud file for media ID {}: {}", id, path);
            storage.delete(path);
            log.info("Deleted cloud file for media ID {}: {}", id, path);
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn("Failed to create URI or extract path for media ID {}. Cloud file might not be deleted. URL: {}",
                    id, m.getMediaUrl(), e);
        } catch (StorageException e) {
            log.error("Error deleting cloud file for media ID {}: {}", id, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during cloud file deletion for media ID {}: {}", id, e.getMessage(), e);
        }

        // 刪除資料庫記錄
        try {
            log.info("Deleting media record from DB: ID={}", id);
            repo.delete(m);
            log.info("Deleted media record from DB: ID={}", id);
        } catch (DataAccessException e) {
            log.error("FATAL Error deleting media record from DB: ID={}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete media record from database for ID " + id, e);
        } catch (Exception e) {
            log.error("FATAL Unexpected error during media database deletion: ID={}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Unexpected error during media database deletion for ID " + id, e);
        }

        // 刪除後重新排序剩餘 media，確保 mediaOrder 連續且主圖在第一位
        reorderMediaAfterDeletion(m.getProduct().getId());

        log.info("Finished deleting single media ID: {}", id);
    }

    /** 刪除後重新排序剩餘 media */
    @Transactional
    private void reorderMediaAfterDeletion(Integer productId) {
        log.info("Reordering remaining media for product ID {} after deletion.", productId);
        List<ProductMedia> remains = repo.findByProductId(productId);

        // 將主圖移到列表最前面，然後按 mediaOrder 排序
        remains.sort((a, b) -> {
            if (a.getIsMain() && !b.getIsMain())
                return -1;
            if (!a.getIsMain() && b.getIsMain())
                return 1;
            return a.getMediaOrder() - b.getMediaOrder();
        });

        // 重新編號 mediaOrder，從 1 開始
        for (int i = 0; i < remains.size(); i++) {
            remains.get(i).setMediaOrder(i + 1);
        }

        // 保存重新排序的結果
        try {
            log.info("Saving reordered remaining media for product ID {}. Count: {}", productId, remains.size());
            repo.saveAll(remains);
            log.info("Successfully saved reordered remaining media for product ID {}.", productId);
        } catch (DataAccessException e) {
            log.error("Failed to save reordered remaining media to DB for product ID {}", productId, e);
            throw new RuntimeException("保存媒體排序失敗", e);
        } catch (Exception e) {
            log.error("Unexpected error saving reordered remaining media for product ID {}", productId, e);
            throw new RuntimeException("保存媒體排序時發生未知錯誤", e);
        }
    }

    /** 設置主圖 */
    @Transactional
    public void setMain(Integer mediaId) {
        log.info("Attempting to set media ID {} as main.", mediaId);
        ProductMedia media = repo.findById(mediaId)
                .orElseThrow(() -> {
                    log.warn("Media not found for setting as main. ID: {}", mediaId);
                    return new IllegalArgumentException("Media not found: " + mediaId);
                });
        log.info("Found media ID {} for setting as main. Product ID: {}", mediaId, media.getProduct().getId());

        // 取消同商品下的舊主圖
        repo.findFirstByProductIdAndIsMainTrue(media.getProduct().getId()).ifPresent(oldMain -> {
            if (!oldMain.getId().equals(mediaId)) {
                log.info("Unsetting old main media ID {} for product ID {}", oldMain.getId(),
                        media.getProduct().getId());
                oldMain.setIsMain(false);
                repo.save(oldMain);
            }
        });

        // 設定新主圖
        media.setIsMain(true);
        try {
            log.info("Setting media ID {} as main and saving.", mediaId);
            repo.save(media);

            // 設定主圖後，觸發重新排序，將其移到第一位
            reorderMediaAfterMainSet(media.getProduct().getId(), mediaId);

            log.info("Successfully set media ID {} as main.", mediaId);
        } catch (DataAccessException e) {
            log.error("Failed to save media record when setting as main for ID {}", mediaId, e);
            throw new RuntimeException("設定主圖失敗", e);
        } catch (Exception e) {
            log.error("Unexpected error when saving media record when setting as main for ID {}", mediaId, e);
            throw new RuntimeException("設定主圖時發生未知錯誤", e);
        }

        log.info("Finished setting media ID {} as main.", mediaId);
    }

    /** 設定主圖後重新排序，將主圖移到第一位 */
    @Transactional
    private void reorderMediaAfterMainSet(Integer productId, Integer mainMediaId) {
        log.info("Reordering media for product ID {} after setting main media ID {}.", productId, mainMediaId);
        List<ProductMedia> mediaList = repo.findByProductId(productId);

        // 將主圖移到列表最前面
        mediaList.sort((a, b) -> {
            if (a.getId().equals(mainMediaId))
                return -1; // 主圖排第一
            if (b.getId().equals(mainMediaId))
                return 1;
            return a.getMediaOrder() - b.getMediaOrder(); // 其他按原 order 排序
        });

        // 重新編號 mediaOrder，從 1 開始
        for (int i = 0; i < mediaList.size(); i++) {
            mediaList.get(i).setMediaOrder(i + 1);
        }

        // 保存重新排序的結果
        try {
            log.info("Saving reordered media for product ID {} after main set. Count: {}", productId,
                    mediaList.size());
            repo.saveAll(mediaList);
            log.info("Successfully saved reordered media for product ID {}.", productId);
        } catch (DataAccessException e) {
            log.error("Failed to save reordered media to DB for product ID {} after main set.", productId, e);
            throw new RuntimeException("保存媒體排序失敗", e);
        } catch (Exception e) {
            log.error("Unexpected error saving reordered media for product ID {} after main set.", productId, e);
            throw new RuntimeException("保存媒體排序時發生未知錯誤", e);
        }
    }

    /** 刪除某商品下的所有 media 並移除雲端檔案 */
    @Transactional
    public void deleteAllByProductId(Integer productId) {
        log.info("Starting to delete all media for product ID: {}", productId);

        log.info("Finding media by product ID {} with pessimistic write lock.", productId);
        List<ProductMedia> mediaList = repo.findByProductId(productId);
        log.info("Found {} media records for product ID: {}", mediaList.size(), productId);

        if (mediaList.isEmpty()) {
            log.info("No media found for product ID {}, returning.", productId);
            return;
        }

        List<String> cloudPathsToDelete = new ArrayList<>();

        log.info("Collecting cloud paths for product ID: {}", productId);
        for (ProductMedia media : mediaList) {
            try {
                URI uri = URI.create(media.getMediaUrl());
                String path = uri.getPath();
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }
                cloudPathsToDelete.add(path);
                log.debug("Collected media ID {} with path {}", media.getId(), path);
            } catch (IllegalArgumentException | NullPointerException e) {
                log.warn("Failed to create URI or extract path for media ID {}. Cloud file path not added for batch deletion. URL: {}",
                        media.getId(), media.getMediaUrl(), e);
            }
        }
        log.info("Collected {} cloud paths for product ID: {}", cloudPathsToDelete.size(), productId);

        log.info("Attempting to delete {} media records from DB individually for product ID: {}", mediaList.size(),
                productId);
        try {
            for (ProductMedia media : mediaList) {
                repo.delete(media);
                log.debug("Deleted media record from DB: ID={}", media.getId());
            }
            log.info("Successfully deleted media records from DB individually for product ID {}. Count: {}",
                    productId, mediaList.size());
        } catch (DataAccessException e) {
            log.error("FATAL Error deleting media records from DB for product ID {}: {}", productId, e.getMessage(),
                    e);
            throw new RuntimeException("Failed to delete media records from database for product ID " + productId,
                    e);
        } catch (Exception e) {
            log.error("FATAL Unexpected error during media database deletion for product ID {}: {}", productId,
                    e.getMessage(), e);
            throw new RuntimeException("Unexpected error during media database deletion for product ID " + productId,
                    e);
        }

        log.info("Attempting to batch delete {} cloud files for product ID: {}", cloudPathsToDelete.size(),
                productId);
        for (String path : cloudPathsToDelete) {
            try {
                log.debug("Deleting cloud file: {}", path);
                storage.delete(path);
                log.debug("Deleted cloud file: {}", path);
            } catch (Exception e) { // 這裡捕獲 Exception，因為 storage.delete 已經內部處理或拋出 StorageException
                log.warn("Failed to delete cloud file {}. Error: {}", path, e.getMessage(), e);
                // 不重新拋出，允許繼續刪除其他雲端檔案
            }
        }
        log.info("Finished attempting to delete cloud files for product ID: {}", productId);

        log.info("Finished deleting all media for product ID: {}", productId);
    }
}