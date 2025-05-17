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
public class OrderDetailResponseDTO {
    private OrderResponseDTO order; // 主要訂單資訊(擴充資料保留用)
    private List<OrderItemResponseDTO> orderDetails; // 明細
}
