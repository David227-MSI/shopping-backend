package tw.eeits.unhappy.ttpp.media.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.eeits.unhappy.ttpp.media.enums.MediaType;

@Data
public class MediaRequest {

    @NotNull(message = "關聯ID 為必要資訊")
    private Integer id;

    @NotNull(message = "媒體種類 為必要資訊")
    private MediaType mediaType;

    @NotNull(message = "媒體數據 為必要資訊")
    private MultipartFile mediaData;

}
