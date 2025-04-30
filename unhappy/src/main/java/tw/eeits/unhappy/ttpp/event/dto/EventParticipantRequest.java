package tw.eeits.unhappy.ttpp.event.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EventParticipantRequest {

    @NotNull(message = "userId 不可為空值")
    private Integer userId;
    @NotNull(message = "prizeId 不可為空值")
    private Integer prizeId;
    @NotNull(message = "eventId 不可為空值")
    private Integer eventId;
}
