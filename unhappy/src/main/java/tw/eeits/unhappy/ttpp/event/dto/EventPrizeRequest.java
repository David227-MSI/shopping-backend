package tw.eeits.unhappy.ttpp.event.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import tw.eeits.unhappy.ttpp.event.enums.PrizeType;

@Data
public class EventPrizeRequest {

    private Integer eventId;
    private Integer itemId;

    @NotNull(message = "itemType 不可為空值")
    private PrizeType itemType;

    @NotNull(message = "quantity 不可為空值")
    @PositiveOrZero(message = "quantity 必須 >= 0")
    private Integer quantity;

    @DecimalMin(value = "0.0", inclusive = true, message = "winRate 必須 >= 0")
    @DecimalMax(value = "1.0", inclusive = true, message = "winRate 必須 <= 1")
    private BigDecimal winRate;

    @NotNull(message = "Total slots 不可為空值")
    @Positive(message = "Total slots 必須 > 0")
    private Integer totalSlots;

    @NotBlank(message = "Title 不可為空值")
    private String title;
}
