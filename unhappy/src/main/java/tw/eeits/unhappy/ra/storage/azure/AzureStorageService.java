package tw.eeits.unhappy.ra.storage.azure;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import tw.eeits.unhappy.ra.storage.StorageService;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AzureStorageService implements StorageService {

    private final BlobContainerClient container;

    @Override
    public String upload(String path, InputStream in, long size, String ct) {
        var blob = container.getBlobClient(path);
        blob.upload(in, size, true);                        // 覆寫同名
        blob.setHttpHeaders(new BlobHttpHeaders().setContentType(ct));

        log.info("Upload [{}] OK → {}", path, blob.getBlobUrl());
        return blob.getBlobUrl();
    }

    @Override
    public void delete(String path) {
        var deleted = container.getBlobClient(path).deleteIfExists();
        log.info("Delete [{}] = {}", path, deleted);
    }
}
