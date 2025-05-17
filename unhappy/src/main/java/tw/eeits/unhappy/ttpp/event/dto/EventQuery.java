package tw.eeits.unhappy.ttpp.event.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EventQuery {
    private String eventName;
    private LocalDateTime announceTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
