package tw.eeits.unhappy.ttpp.event.model;

import java.math.BigDecimal;
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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import tw.eeits.unhappy.ttpp.event.enums.EventStatus;

@Entity
@Table(name = "event")
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Event name 不可為空值")
    @Size(max = 100, message = "Event name 不可超過100字")
    @Column(name = "event_name", nullable = false, length = 100)
    private String eventName;

    @NotNull(message = "Min spend 不可為空值")
    @Min(value = 0, message = "Min spend 必須 >= 0")
    @Column(name = "min_spend", nullable = false, precision = 15, scale = 2)
    private BigDecimal minSpend = BigDecimal.ZERO;

    @NotNull(message = "Max entries 不可為空值")
    @Min(value = 0, message = "Max entries 必須 >= 0")
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
}