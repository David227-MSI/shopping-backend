package tw.eeits.unhappy.ttpp.notification.dto;

import java.time.LocalDateTime;

import lombok.Data;
import tw.eeits.unhappy.ttpp.notification.enums.NoticeType;

@Data
public class NotificationQuery {
    // published
    private Integer userId;
    private Boolean isRead;
    private LocalDateTime createdAt;
    // template
    private String title;
    private NoticeType noticeType;
}
