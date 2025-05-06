package tw.eeits.unhappy.ttpp.event.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import tw.eeits.unhappy.ttpp.media.enums.MediaType;

@Data
public class EventRequest {

    private Integer id;

    @NotNull(message = "Event name 不可為空值")
    @Size(max = 100, message = "Event name 不可超過100字")
    private String eventName;

    @NotNull(message = "Min spend 不可為空值")
    @Min(value = 0, message = "Min spend 必須 >= 0")
    private BigDecimal minSpend;

    @NotNull(message = "Max entries 不可為空值")
    @Min(value = 0, message = "Max entries 必須 >= 0")
    private Integer maxEntries;

    @NotNull(message = "Start time 不可為空值")
    private LocalDateTime startTime;

    @NotNull(message = "End time 不可為空值")
    private LocalDateTime endTime;

    @NotNull(message = "Announce time 不可為空值")
    private LocalDateTime announceTime;

    @NotNull(message = "Established by 不可為空值")
    @Size(max = 100, message = "Established by 不可超過100字")
    private String establishedBy;

    // @NotNull(message = "媒體種類 為必要資訊")
    private MediaType mediaType;

    @NotNull(message = "媒體數據 為必要資訊")
    private MultipartFile mediaData;

}
