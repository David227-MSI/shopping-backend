package tw.eeits.unhappy.gy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequestDTO {
    private Integer userId;
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
    private List<OrderItemRequestDTO> items; // 購買商品明細
    private String couponPublishedId; // 優惠券可以為空
}
