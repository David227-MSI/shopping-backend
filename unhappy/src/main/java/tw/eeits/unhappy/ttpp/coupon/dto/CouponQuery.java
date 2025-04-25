package tw.eeits.unhappy.ttpp.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import tw.eeits.unhappy.ttpp.coupon.enums.ApplicableType;
import tw.eeits.unhappy.ttpp.coupon.enums.DiscountType;

@Data
public class CouponQuery {
    // for published coupon
    private Integer userId;
    private Boolean isUsed;

    // for template coupon
    private ApplicableType applicableType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal minDiscountValue;
    private BigDecimal maxDiscountValue;
    private DiscountType discountType;
}

