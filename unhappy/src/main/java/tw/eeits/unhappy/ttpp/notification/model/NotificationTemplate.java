package tw.eeits.unhappy.ttpp.notification.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.eeits.unhappy.ttpp.notification.enums.NoticeType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notification_template")
public class NotificationTemplate {

    
    // mapped: fk_notification_published_notification_template
    @Builder.Default
    @OneToMany(mappedBy = "notificationTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationPublished> notificationPublished = new ArrayList<>();


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Title 不可為空值")
    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @NotNull(message = "Notice type 不可為空值")
    @Column(name = "notice_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private NoticeType noticeType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;



    @PrePersist
    protected void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }


    // mapped: fk_notification_published_notification_template
    public void addNotificationPublished(NotificationPublished published) {
        notificationPublished.add(published);
        published.setNotificationTemplate(this);
    }
    public void removeNotificationPublished(NotificationPublished published) {
        notificationPublished.remove(published);
        published.setNotificationTemplate(null);
    }
}