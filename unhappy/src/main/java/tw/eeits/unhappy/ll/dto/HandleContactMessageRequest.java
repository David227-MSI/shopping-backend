package tw.eeits.unhappy.ll.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HandleContactMessageRequest {
    @NotNull(message = "處理狀態不可為空")
    private Boolean isHandled;
    private String note;
}