package tw.eeits.unhappy.ttpp.event.model;

import java.math.BigDecimal;
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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.eeits.unhappy.ttpp.event.enums.EventStatus;
import tw.eeits.unhappy.ttpp.media.model.EventMedia;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "event")
public class Event {

    
    // mapped: fk_event_prize_event
    @Builder.Default
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventPrize> eventPrize = new ArrayList<>();
    
    // mapped: fk_event_participant_event
    @Builder.Default
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventParticipant> eventParticipants = new ArrayList<>();
    
    // mapped: fk_event_media_event
    @Builder.Default
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventMedia> eventMedia = new ArrayList<>();


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Event name 不可為空值")
    @Size(max = 100, message = "Event name 不可超過100字")
    @Column(name = "event_name", nullable = false, length = 100)
    private String eventName;

    @NotNull(message = "Min spend 不可為空值")
    @Min(value = 0, message = "Min spend 必須 >= 0")
    @Builder.Default
    @Column(name = "min_spend", nullable = false, precision = 15, scale = 2)
    private BigDecimal minSpend = BigDecimal.ZERO;

    @NotNull(message = "Max entries 不可為空值")
    @Min(value = 0, message = "Max entries 必須 >= 0")
    @Builder.Default
    @Column(name = "max_entries", nullable = false)
    private Integer maxEntries = 1;

    @NotNull(message = "Start time 不可為空值")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @NotNull(message = "End time 不可為空值")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @NotNull(message = "Announce time 不可為空值")
    @Column(name = "announce_time", nullable = false)
    private LocalDateTime announceTime;

    @NotNull(message = "Event status 不可為空值")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "event_status", nullable = false, length = 10)
    private EventStatus eventStatus = EventStatus.ANNOUNCED;

    @NotNull(message = "Established by 不可為空值")
    @Size(max = 100, message = "Established by 不可超過100字")
    @Column(name = "established_by", nullable = false, length = 100)
    private String establishedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // mapped: fk_event_prize_event
    public void addEventPrize(EventPrize prize) {
        eventPrize.add(prize);
        prize.setEvent(this);
    }
    public void removeEventPrize(EventPrize prize) {
        eventPrize.remove(prize);
        prize.setEvent(null);
    }

    // mapped: fk_event_participant_event
    public void addEventParticipant(EventParticipant participant) {
        eventParticipants.add(participant);
        participant.setEvent(this);
    }
    public void removeEventParticipant(EventParticipant participant) {
        eventParticipants.remove(participant);
        participant.setEvent(null);
    }

    // mapped: fk_event_media_event
    public void addEventMedia(EventMedia media) {
        eventMedia.add(media);
        media.setEvent(this);
    }
    public void removeEventMedia(EventMedia media) {
        eventMedia.remove(media);
        media.setEvent(null);
    }

}