package tw.eeits.unhappy.ttpp.media.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.eeits.unhappy.ttpp.media.enums.MediaType;

@Data
public class EventMediaRequest {

    @NotNull(message = "活動ID 不可為空值")
    private Integer eventId;

    @NotNull(message = "mediaData 不可為空值")
    private byte[] mediaData;

    @NotNull(message = "mediaType 不可為空值")
    private MediaType mediaType;


}
