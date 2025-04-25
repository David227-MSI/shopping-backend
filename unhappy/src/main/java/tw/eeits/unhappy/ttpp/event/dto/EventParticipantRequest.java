package tw.eeits.unhappy.ttpp.event.dto;

import lombok.Data;

@Data
public class EventParticipantRequest {

    private Integer userId;
    private Integer prizeId;
    private Integer eventId;
}
