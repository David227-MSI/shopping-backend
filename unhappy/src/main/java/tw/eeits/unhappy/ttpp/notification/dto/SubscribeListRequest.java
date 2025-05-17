package tw.eeits.unhappy.ttpp.notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.eeits.unhappy.ttpp.notification.enums.ItemType;

@Data
public class SubscribeListRequest {
    
    @NotNull(message = "用戶ID 不可為空值")
    private Integer userId; // fk

    @NotNull(message = "訂閱目標ID 不可為空值")
    private Integer itemId;

    @NotNull(message = "訂閱目標種類 不可為空值")
    private ItemType itemType;
}
