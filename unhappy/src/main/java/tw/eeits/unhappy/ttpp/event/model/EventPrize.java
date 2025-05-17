package tw.eeits.unhappy.ttpp.event.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.eeits.unhappy.ttpp.event.enums.PrizeType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "event_prize")
public class EventPrize {

    // fk_event_prize_event
    @NotNull(message = "event 不可為空值")
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // mapped: fk_event_participant_event_prize
    @Builder.Default
    @OneToMany(mappedBy = "eventPrize", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<EventParticipant> eventParticipants = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "itemId 不可為空值")
    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @NotNull(message = "itemType 不可為空值")
    @Column(name = "item_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private PrizeType itemType;

    @NotNull(message = "quantity 不可為空值")
    @PositiveOrZero(message = "quantity 必須 >= 0")
    @Builder.Default
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @NotNull(message = "winRate 不可為空值")
    @DecimalMin(value = "0.0", inclusive = true, message = "winRate 必須 >= 0")
    @DecimalMax(value = "1.0", inclusive = true, message = "winRate 必須 <= 1")
    @Builder.Default
    @Column(name = "win_rate", nullable = false, precision = 18, scale = 4)
    private BigDecimal winRate = new BigDecimal("0.2000");

    @NotNull(message = "Total slots 不可為空值")
    @Positive(message = "Total slots 必須 > 0")
    @Column(name = "total_slots", nullable = false)
    private Integer totalSlots;

    @NotNull(message = "Remaining slots 不可為空值")
    @PositiveOrZero(message = "Remaining slots 必須 >= 0")
    @Column(name = "remaining_slots", nullable = false)
    private Integer remainingSlots;

    @NotBlank(message = "Title 不可為空值")
    @Column(name = "title", length = 100, nullable = false)
    private String title;

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

    // mapped: fk_event_participant_event_prize
    public void addEventParticipant(EventParticipant participant) {
        eventParticipants.add(participant);
        participant.setEventPrize(this);
    }
    public void removeEventParticipant(EventParticipant participant) {
        eventParticipants.remove(participant);
        participant.setEventPrize(null);
    }

}
