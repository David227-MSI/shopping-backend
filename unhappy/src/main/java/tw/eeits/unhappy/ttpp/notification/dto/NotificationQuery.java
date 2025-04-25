package tw.eeits.unhappy.ttpp.notification.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.eeits.unhappy.ttpp.notification.enums.NoticeType;

@Data
public class NotificationQuery {
    // published
    @NotNull(message = "用戶ID 不可為空值")
    private Integer userId;
    private Boolean isRead;
    private LocalDateTime createdAt;
    // template
    private String title;
    private NoticeType noticeType;
}
