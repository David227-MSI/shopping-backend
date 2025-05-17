package tw.eeits.unhappy.ttpp.coupon.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CouponPublishedRequest {

    @Size(min = 36, max = 36, message = "id 必須為 36 字元")
    private String id;

    @NotNull(message = "couponTemplate ID 不可為空值")
    private Integer couponTemplateId;

    @NotNull(message = "user ID 不可為空值")
    private Integer userId; // fk
    
}
