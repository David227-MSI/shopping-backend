package tw.eeits.unhappy.ttpp._itf;

import tw.eeits.unhappy.ttpp.coupon.model.CouponPublished;
import tw.eeits.unhappy.ttpp.coupon.model.CouponTemplate;

public interface CouponService {

    // 建立優惠相關======================================================
    /**
     * @brief          建立優惠券的模板
     * @param template 優惠券物件
     * @return         建立成功: 優惠券物件 / 建立失敗: Null
     */
    CouponTemplate createTemplate(CouponTemplate template);

    /**
     * @brief                 發送優惠券給用戶
     * @param couponPublished 預計要發送的優惠券
     * @return                建立成功: 優惠券物件 / 建立失敗: Null
     */
    CouponPublished publishCoupon(CouponPublished couponPublished);
    // 建立優惠相關======================================================
    
}
