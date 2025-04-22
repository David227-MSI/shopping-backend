package tw.eeits.unhappy.ttpp.event.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    
    // fk_event_participant_user
    // @ManyToOne
    // @JoinColumn(name = "user_id", nullable = false)
    // @NotNull(message = "userMember不可為空值")
    // private UserMember userMember;
    // |||                |||
    // vvv to be replaced vvv
    @NotNull(message = "userId 不可為空值")
    @Column(name = "user_id", nullable = false)
    private Integer userId; // fk

    // fk_event_participant_event_prize
    @ManyToOne
    @JoinColumn(name = "event_prize_id", nullable = false)
    @NotNull(message = "eventPrize 不可為空值")
    private EventPrize eventPrize;
    
    // fk_event_participant_event
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @NotNull(message = "event 不可為空值")
    private Event event;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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
