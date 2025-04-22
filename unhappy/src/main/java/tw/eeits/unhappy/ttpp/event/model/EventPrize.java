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
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import tw.eeits.unhappy.ttpp.notification.enums.ItemType;

@Entity
@Table(name = "event_prize")
@Data
public class EventPrize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "eventId 不可為空值")
    @Column(name = "event_id", nullable = false)
    private Integer eventId;

    @NotNull(message = "itemId 不可為空值")
    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @NotNull(message = "itemType 不可為空值")
    @Column(name = "item_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    @NotNull(message = "quantity 不可為空值")
    @PositiveOrZero(message = "quantity 必須 >= 0")
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @NotNull(message = "winRate 不可為空值")
    @DecimalMin(value = "0.0", inclusive = true, message = "winRate 必須 >= 0")
    @DecimalMax(value = "1.0", inclusive = true, message = "winRate 必須 <= 1")
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
}
