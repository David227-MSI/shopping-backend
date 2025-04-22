package tw.eeits.unhappy.ttpp.notification.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "notification_published")
@Data
public class NotificationPublished {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "notification_id", nullable = false)
    @NotNull(message = "notificationId 不可為空值")
    private Integer notificationId;

    @Column(name = "user_id", nullable = false)
    @NotNull(message = "userId 不可為空值")
    private Integer userId; // fk

    @Column(name = "is_read", nullable = false)
    @NotNull(message = "isRead 不可為空值")
    private Boolean isRead = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @PrePersist
    protected void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
