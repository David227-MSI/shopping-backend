package tw.eeits.unhappy.ra.review.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ra.storage.StorageService;

@Service
@RequiredArgsConstructor
public class ReviewMediaService {

    private static final long MAX = 3 * 1024 * 1024;      // 3 MB
    private final StorageService storage;

    public String upload(Integer userId, MultipartFile file) throws IOException {

        if (file.getSize() > MAX) throw new IllegalArgumentException("檔案 ≤ 3 MB");

        String ext  = FilenameUtils.getExtension(file.getOriginalFilename());
        String path = "review/%d/%s.%s"
                        .formatted(userId, UUID.randomUUID(), ext);


        return storage.upload(
            path,
            file.getInputStream(),
            file.getSize(),
            file.getContentType());
    }
}
