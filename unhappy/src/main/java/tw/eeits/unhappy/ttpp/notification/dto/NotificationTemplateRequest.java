package tw.eeits.unhappy.ttpp.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.eeits.unhappy.ttpp.notification.enums.NoticeType;

@Data
public class NotificationTemplateRequest {
    @NotBlank(message = "訊息標題 不可為空值")
    private String title;

    private String content;

    @NotNull(message = "通知類型 不可為空值")
    private NoticeType noticeType;
}

