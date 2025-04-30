package tw.eeits.unhappy.ra.storage.azure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("azure.storage")
public record AzureProps(String connectionString, String container) {
    
}