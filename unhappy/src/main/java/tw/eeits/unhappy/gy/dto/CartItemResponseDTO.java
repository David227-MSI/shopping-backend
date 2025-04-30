package tw.eeits.unhappy.gy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponseDTO {
    private Integer id;
    private Integer productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
    private Boolean checkedOut;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
