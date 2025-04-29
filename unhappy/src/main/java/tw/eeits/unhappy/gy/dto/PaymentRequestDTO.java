package tw.eeits.unhappy.gy.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {

    private Integer orderId;
    private BigDecimal amount; // 最後折扣後金額
}
