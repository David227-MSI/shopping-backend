package tw.eeits.unhappy.ttpp.coupon.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderSelectCouponRequest {

    @NotNull(message = "用戶ID 為必要資訊")
    private Integer userId;
    @NotNull(message = "訂單總金額 為必要資訊")
    private BigDecimal totalAmount;
    @NotNull(message = "下單商品清單 為必要資訊")
    private List<Integer> productIds;
}
