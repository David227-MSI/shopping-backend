package tw.eeits.unhappy.ttpp.notification.model;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.eeits.unhappy.eee.domain.UserMember;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notification_published")
public class NotificationPublished {


    // fk_notification_published_notification_template
    @ManyToOne
    @NotNull(message = "notificationTemplate 不可為空值")
    @JoinColumn(name = "notification_id")
    private NotificationTemplate notificationTemplate;

    // fk_notification_published_user
    @ManyToOne
    @NotNull(message = "userMember 不可為空值")
    @JoinColumn(name = "user_id", nullable = false)
    private UserMember userMember;
    // |||                |||
    // vvv to be replaced vvv
    // @Column(name = "user_id", nullable = false)
    // @NotNull(message = "userId 不可為空值")
    // private Integer userId; // fk

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "isRead 不可為空值")
    @Builder.Default
    @Column(name = "is_read", nullable = false)
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
