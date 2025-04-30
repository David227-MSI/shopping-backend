package tw.eeits.unhappy.ttpp.coupon.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CouponTransferRequest {

    @NotBlank(message = "優惠券ID 為必要欄位")
    private String couponId;
    @NotBlank(message = "受贈者信箱 為必要欄位")
    private String recipientMail;
}
