package tw.eeits.unhappy.ra.storage.azure;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
public class StorageConfig {

    @Bean
    @ConditionalOnProperty(name = "azure.storage.connection-string", matchIfMissing = false)
    public BlobContainerClient blobContainerClient(
            @Value("${azure.storage.connection-string}") String conn,
            @Value("${azure.storage.container}")        String container) {

        return new BlobContainerClientBuilder()
                    .connectionString(conn)
                    .containerName(container)
                    .buildClient();
    }
}