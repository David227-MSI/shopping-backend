package tw.eeits.unhappy.ra.review.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tw.eeits.unhappy.ra.storage.StorageService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewMediaService {

    private static final long MAX = 3 * 1024 * 1024; // 3 MB
    private final StorageService storage;

    public String upload(Integer userId, MultipartFile file) throws IOException {
        if (file.getSize() > MAX) {
            log.warn("圖片過大: {} ({} bytes), 限制為 {} bytes", file.getOriginalFilename(), file.getSize(), MAX);
            throw new IllegalArgumentException("檔案 ≤ 3 MB");
        }

        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        String path = "review/%d/%s.%s".formatted(userId, UUID.randomUUID(), ext);
        log.info("上傳圖片: userId={}, path={}", userId, path);

        String url = storage.upload(
            path,
            file.getInputStream(),
            file.getSize(),
            file.getContentType());
        log.info("圖片上傳成功: url={}", url);
        return url;
    }

    public List<String> uploadMultiple(Integer userId, MultipartFile[] files) throws IOException {
        List<String> urls = new ArrayList<>();
        log.info("接收到圖片數量: {}", files != null ? files.length : 0);
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        String url = upload(userId, file);
                        urls.add(url);
                    } catch (IOException e) {
                        log.error("圖片上傳失敗: {} - {}", file.getOriginalFilename(), e.getMessage());
                        throw e;
                    }
                } else {
                    log.warn("忽略空圖片文件");
                }
            }
        }
        log.info("上傳完成, 返回 URL 數量: {}", urls.size());
        return urls;
    }
}