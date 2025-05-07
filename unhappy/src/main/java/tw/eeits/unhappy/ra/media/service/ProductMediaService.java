package tw.eeits.unhappy.ra.media.service;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.springframework.dao.DataAccessException; // 引入 DataAccessException
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 引入 Slf4j

import tw.eeits.unhappy.eeit198product.entity.Product;
import tw.eeits.unhappy.ra.media.model.MediaType;
import tw.eeits.unhappy.ra.media.model.ProductMedia;
import tw.eeits.unhappy.ra.media.repository.ProductMediaRepository;
import tw.eeits.unhappy.ra.storage.StorageService;
import tw.eeits.unhappy.ra.storage.StorageException; // 引入 StorageException


@Slf4j // 添加 Slf4j 日誌註解
@Service
@RequiredArgsConstructor
public class ProductMediaService {

    private static final long MAX_SIZE = 5 * 1024 * 1024;   // 5 MB 上限

    private final ProductMediaRepository repo;
    private final StorageService storage;

    /** 上傳並建立一筆 media；若 main=true 會自動取消舊主圖 */
    @Transactional
    public ProductMedia upload(Integer productId,
                                MultipartFile file,
                                boolean main,
                                Integer order) throws IOException {

        log.info("Attempting to upload media for product ID: {}", productId);
        // 0. 檔案大小驗證
        if (file.getSize() > MAX_SIZE) {
            log.warn("File size exceeds limit for product ID {}. Size: {}", productId, file.getSize());
            throw new IllegalArgumentException("檔案大小不得超過 " + MAX_SIZE / 1024 / 1024 + " MB");
        }

        // 1. 儲存到雲端
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        String path = "product-media/%d/%s.%s".formatted(productId, UUID.randomUUID(), ext); // 建議使用更明確的路徑，例如 product-media/商品ID/UUID.ext
        String mediaUrl;
        try {
             log.info("Uploading file to storage: {}", path);
            mediaUrl = storage.upload(
                path,
                file.getInputStream(),
                file.getSize(),
                file.getContentType()
            );
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
        // 需要根據 ProductMedia Entity 的結構來設定 Product 對象或 product_id
        // 如果 ProductMedia 有 ManyToOne Product 欄位
        Product product = new Product(); // 這裡需要一個 Product 對象，只設定 ID 即可
        product.setId(productId);
        media.setProduct(product); // 設定 Product 對象

        media.setMediaType(MediaType.IMAGE); // 假設上傳的都是圖片，如果需要支援影片，需要根據 contentType 判斷
        media.setMediaUrl(mediaUrl);
        media.setAltText(""); // 預設空字串，前端可以編輯
        media.setMediaOrder(finalOrder);
        media.setIsMain(main);

        // 4. 處理主圖設定
        if (main) {
             log.info("Setting new media ID {} as main for product ID {}", media.getId(), productId);
            // 取消同商品下的舊主圖
            repo.findFirstByProductIdAndIsMainTrue(productId).ifPresent(oldMain -> {
                 log.info("Unsetting old main media ID {} for product ID {}", oldMain.getId(), productId);
                oldMain.setIsMain(false);
                repo.save(oldMain); // 保存舊主圖的變更
            });
        }

        // 5. 保存新的 ProductMedia 記錄
        try {
             log.info("Saving new media record for product ID {}", productId);
            ProductMedia savedMedia = repo.save(media);
             log.info("New media record saved successfully. ID: {}", savedMedia.getId());
            return savedMedia;
        } catch (DataAccessException e) {
             log.error("Failed to save media record to DB for product ID {}", productId, e);
             // 如果資料庫保存失敗，可能需要考慮刪除已經上傳到雲端的檔案
             // storage.delete(path); // 這裡可以選擇性地加上雲端檔案回滾邏輯
             throw new RuntimeException("保存媒體記錄失敗", e);
         } catch (Exception e) {
             log.error("Unexpected error during saving media record for product ID {}", productId, e);
             throw new RuntimeException("保存媒體記錄時發生未知錯誤", e);
        }
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

         // 保存所有變更（@Transactional 結束時會自動 flush/commit）
         // 顯式呼叫 saveAll 可以確保立即保存
         try {
              log.info("Saving reordered media for product ID {}", productId);
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
             log.warn("Failed to create URI or extract path for media ID {}. Cloud file might not be deleted. URL: {}", id, m.getMediaUrl(), e);
         } catch (StorageException e) { // 捕獲 StorageService 的異常
             log.error("Error deleting cloud file for media ID {}: {}", id, e.getMessage(), e);
             // TODO: 根據需求決定是否在雲端刪除失敗時停止 DB 刪除
             // 目前選擇記錄錯誤，但不中斷 DB 刪除，因為 DB 記錄是主要的外鍵來源
         } catch (Exception e) { // 捕獲其他未知異常
             log.error("Unexpected error during cloud file deletion for media ID {}: {}", id, e.getMessage(), e);
             // TODO: 根據需求決定是否中斷
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


        // 重新排序剩餘media
        log.info("Reordering remaining media for product ID: {}", m.getProduct().getId());
        List<ProductMedia> remains = repo.findByProductId(m.getProduct().getId())
                .stream()
                .sorted(Comparator.comparing(ProductMedia::getMediaOrder))
                .toList();

        for (int i = 0; i < remains.size(); i++) {
            remains.get(i).setMediaOrder(i + 1);
        }
         // JpaRepository 的 saveAll 方法可以在 Transaction 結束時批量更新
         repo.saveAll(remains); // 顯式保存重新排序的結果
         log.info("Finished reordering remaining media for product ID {}. Count: {}", m.getProduct().getId(), remains.size());
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
             if (!oldMain.getId().equals(mediaId)) { // 避免取消自己
                 log.info("Unsetting old main media ID {} for product ID {}", oldMain.getId(), media.getProduct().getId());
                 oldMain.setIsMain(false);
                 repo.save(oldMain); // 保存舊主圖的變更
             }
        });

        // 設定新主圖
        media.setIsMain(true);
        try {
             log.info("Setting media ID {} as main and saving.", mediaId);
             repo.save(media); // 保存新主圖的變更
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


    /** 刪除某商品下的所有 media 並移除雲端檔案 */
    @Transactional // 這個方法也需要事務支援
    public void deleteAllByProductId(Integer productId) {
         log.info("Starting to delete all media for product ID: {}", productId);

         // 查找該商品下的所有媒體記錄並鎖行
         // 使用悲觀寫鎖確保在刪除過程中沒有其他操作修改這些媒體
         log.info("Finding media by product ID {} with pessimistic write lock.", productId);
         List<ProductMedia> mediaList = repo.findByProductId(productId); // findByProductId 應該已經有 @Lock(LockModeType.PESSIMISTIC_WRITE)
         log.info("Found {} media records for product ID: {}", mediaList.size(), productId);


         if (mediaList.isEmpty()) {
             log.info("No media found for product ID {}, returning.", productId);
             return; // 如果沒有媒體，直接返回
         }

         // 收集要刪除的雲端檔案路徑
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
                  log.warn("Failed to create URI or extract path for media ID {}. Cloud file path not added for batch deletion. URL: {}", media.getId(), media.getMediaUrl(), e);
              }
         }
        log.info("Collected {} cloud paths for product ID: {}", cloudPathsToDelete.size(), productId);


         // 【修正】逐一刪除資料庫記錄，讓 Hibernate 管理 session 狀態
         log.info("Attempting to delete {} media records from DB individually for product ID: {}", mediaList.size(), productId);
         try {
              for (ProductMedia media : mediaList) {
                   repo.delete(media); // 逐一刪除
                   log.debug("Deleted media record from DB: ID={}", media.getId());
              }
              log.info("Successfully deleted media records from DB individually for product ID {}. Count: {}", productId, mediaList.size());
         } catch (DataAccessException e) { // 捕獲資料庫相關異常
              log.error("FATAL Error deleting media records from DB for product ID {}: {}", productId, e.getMessage(), e);
              // 如果資料庫記錄刪除失敗，必須中斷，否則會導致外鍵錯誤
              throw new RuntimeException("Failed to delete media records from database for product ID " + productId, e);
         } catch (Exception e) { // 捕獲其他非資料庫相關異常
              log.error("FATAL Unexpected error during media database deletion for product ID {}: {}", productId, e.getMessage(), e);
              throw new RuntimeException("Unexpected error during media database deletion for product ID " + productId, e);
         }


         // 批量刪除雲端檔案 - 這裡的失敗相對不那麼致命，可以選擇記錄並繼續
         log.info("Attempting to batch delete {} cloud files for product ID: {}", cloudPathsToDelete.size(), productId);
         for (String path : cloudPathsToDelete) {
             try {
                  log.debug("Deleting cloud file: {}\n", path); // 這裡加了換行，讓日誌更清晰
                  storage.delete(path);
                   log.debug("Deleted cloud file: {}\n", path); // 這裡加了換行
             } catch (Exception e) { // 這裡捕獲 Exception，因為 storage.delete 已經內部處理或拋出 StorageException
                  log.warn("Failed to delete cloud file {}. Error: {}", path, e.getMessage(), e);
                  // 不重新拋出，允許繼續刪除其他雲端檔案
             }
         }
        log.info("Finished attempting to delete cloud files for product ID: {}", productId);


         log.info("Finished deleting all media for product ID: {}", productId);
    }
}
