package tw.eeits.unhappy.ttpp.notification.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.eeits.unhappy.ttpp.notification.enums.ItemType;

@Entity
@Table(name = "subscribe_list")
@Data
public class SubscribeList {



    // fk_subscribe_list_user
    // @ManyToOne
    // @NotNull(message = "userMember 不可為空值")
    // @JoinColumn(name = "user_id", nullable = false)
    // private UserMember userMember;
    // |||                |||
    // vvv to be replaced vvv
    @NotNull(message = "userId 不可為空值")
    @Column(name = "user_id", nullable = false)
    private Integer userId; // fk



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "itemId 不可為空值")
    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @NotNull(message = "itemType 不可為空值")
    @Column(name = "item_type", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    @NotNull(message = "isSubscribing 不可為空值")
    @Column(name = "is_subscribing", nullable = false)
    private Boolean isSubscribing = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    
}
