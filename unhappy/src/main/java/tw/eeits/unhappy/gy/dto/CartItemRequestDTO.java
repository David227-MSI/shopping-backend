package tw.eeits.unhappy.gy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemRequestDTO {
    private Integer userId;
    private Integer productId;
    private Integer quantity;
}
