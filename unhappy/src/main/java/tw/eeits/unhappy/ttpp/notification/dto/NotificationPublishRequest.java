package tw.eeits.unhappy.ttpp.notification.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationPublishRequest {
    @NotNull(message = "用戶ID 不可為空值")
    private Integer userId;
    @NotNull(message = "通知訊息模板ID 不可為空值")
    private Integer templateId;
    private LocalDateTime expiredAt;
}
