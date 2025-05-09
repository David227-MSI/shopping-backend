package tw.eeits.unhappy.ll.service;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ra.storage.StorageService;

@Service
@RequiredArgsConstructor
public class BrandMediaService {

    private static final long MAX_SIZE = 2 * 1024 * 1024;  // 限 2MB

    private final StorageService storage;

    public String uploadLogo(Integer brandId, MultipartFile file) throws IOException {
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("檔案大小不得超過 2 MB");
        }

        String ext  = FilenameUtils.getExtension(file.getOriginalFilename());
        String path = "brand/%d/%s.%s".formatted(brandId, UUID.randomUUID(), ext);

        return storage.upload(
            path,
            file.getInputStream(),
            file.getSize(),
            file.getContentType()
        );
    }

    public void deleteFromStorage(String path) {
        storage.delete(path);
    }
}
