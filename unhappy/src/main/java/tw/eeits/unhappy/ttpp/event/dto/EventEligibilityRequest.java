package tw.eeits.unhappy.ttpp.event.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EventEligibilityRequest {
    @NotNull(message = "userId 不可為空值")
    private Integer userId;
    @NotNull(message = "eventId 不可為空值")
    private Integer eventId;
}
