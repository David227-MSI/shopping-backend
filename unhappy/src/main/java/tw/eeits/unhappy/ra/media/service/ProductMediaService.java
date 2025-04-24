package tw.eeits.unhappy.ra.media.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import tw.eeits.unhappy.ra.media.model.MediaType;
import tw.eeits.unhappy.ra.media.model.ProductMedia;
import tw.eeits.unhappy.ra.media.repository.ProductMediaRepository;
import tw.eeits.unhappy.ra.storage.StorageService;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductMediaService {

    private static final long MAX_SIZE = 5 * 1024 * 1024;   // 5 MB 上限
    
    private final ProductMediaRepository repo;
    private final StorageService storage;

    // 上傳並建立一筆 media；若 main=true 會自動取消舊主圖
    @Transactional
    public ProductMedia upload(Integer productId,
                                MultipartFile file,
                                boolean main,
                                Integer order) throws Exception {

        // 0. 檔案大小驗證
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("檔案大小不得超過 5 MB");
        }

        // 1. 產生 mediaOrder
        int finalOrder = (order != null)
                        ? order
                        : Math.toIntExact(repo.countByProductId(productId)) + 1;

        // 2. 上傳至 Blob
        String ext  = FilenameUtils.getExtension(file.getOriginalFilename());
        String path = "%d/%s.%s".formatted(productId, UUID.randomUUID(), ext);
        String url  = storage.upload(path,
                                    file.getInputStream(),
                                    file.getSize(),
                                    file.getContentType());

        // 3. 寫入 DB
        var media = new ProductMedia();
        media.setProductId(productId);
        media.setMediaType(file.getContentType().startsWith("video")
                ? MediaType.VIDEO : MediaType.IMAGE);
        media.setMediaUrl(url);
        media.setAltText(file.getOriginalFilename());
        media.setIsMain(main);
        media.setMediaOrder(finalOrder);

        if (main) { // 取消舊主圖
            repo.findFirstByProductIdAndIsMainTrue(productId)
                    .ifPresent(m -> m.setIsMain(false));
        }
        return repo.save(media);
    }

    /** 拖曳排序後一次重排，避免 UNIQUE 衝突 */
    @Transactional
    public void reorder(Integer productId,
                        Map<Integer, Integer> newOrders) {

        List<ProductMedia> list = repo.findByProductId(productId); // 行鎖
        list.forEach(m -> {
            Integer o = newOrders.get(m.getId());
            if (o != null && !o.equals(m.getMediaOrder())) {
                m.setMediaOrder(o);
            }
        });
        // Transaction commit 時一次 flush
    }

    /** 刪除 media 並移除雲端檔案，再重新編號 */
    @Transactional
    public void delete(Integer id) {
        ProductMedia m = repo.findById(id)
        .orElseThrow(() ->
            new IllegalArgumentException("Media not found: " + id));

        storage.delete(URI.create(m.getMediaUrl()).getPath().substring(1));
        repo.delete(m);

        // 重新拿出同商品其餘 media，依 mediaOrder 重新 1…n
        var remains = repo.findByProductId(m.getProductId())
                            .stream()
                            .sorted((a,b)->a.getMediaOrder()-b.getMediaOrder())
                            .toList();

        for (int i = 0; i < remains.size(); i++) {
            remains.get(i).setMediaOrder(i + 1);
        }
    }
}
