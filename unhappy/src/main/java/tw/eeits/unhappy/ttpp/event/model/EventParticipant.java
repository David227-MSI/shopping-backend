package tw.eeits.unhappy.ttpp.event.model;

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
import tw.eeits.unhappy.ttpp.event.enums.ParticipateStatus;

@Entity
@Table(name = "event_participant")
@Data
public class EventParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "userId 不可為空值")
    @Column(name = "user_id", nullable = false)
    private Integer userId; // fk

    @NotNull(message = "eventPrizeId 不可為空值")
    @Column(name = "event_prize_id", nullable = false)
    private Integer eventPrizeId;

    @NotNull(message = "eventId 不可為空值")
    @Column(name = "event_id", nullable = false)
    private Integer eventId; // fk

    @NotNull(message = "Participate status 不可為空值")
    @Column(name = "participate_status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private ParticipateStatus participateStatus = ParticipateStatus.REGISTERED;

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
