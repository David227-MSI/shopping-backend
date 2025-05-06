package tw.eeits.unhappy.ttpp.notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscribeQuery {

    @NotNull(message = "用戶ID 為必要欄位")
    private Integer userId;

    private Integer categoryId; 
    private String keyword;
}
