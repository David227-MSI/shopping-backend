package tw.eeits.unhappy.ra.storage;

import java.io.InputStream;

public interface StorageService {

    // 上傳檔案並回傳公開（或 SAS）URL
    String upload(String path,
                InputStream in,
                long size,
                String contentType);

    // 刪除指定路徑的檔案（若不存在也不拋例外）
    void delete(String path);
}