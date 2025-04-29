package tw.eeits.unhappy.gy.payment.ecpay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// 讀取 application.yml 中的 ecpay 區塊，方便日後更換正式版(不寫死)
@Data
@Configuration
@ConfigurationProperties(prefix = "ecpay")

public class EcpayProperties {
    private String merchantId;
    private String hashKey;
    private String hashIv;
    private String apiUrl;
}
