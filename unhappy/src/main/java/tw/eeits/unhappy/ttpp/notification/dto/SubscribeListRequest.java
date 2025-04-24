package tw.eeits.unhappy.ttpp.notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.eeits.unhappy.ttpp.notification.enums.ItemType;

@Data
public class SubscribeListRequest {
    
    private Integer id;
    
    private Integer userId; // fk

    @NotNull(message = "itemId 不可為空值")
    private Integer itemId;

    @NotNull(message = "itemType 不可為空值")
    private ItemType itemType;
}
