package tw.eeits.unhappy.gy.exception;

public class CouponNotFoundException extends RuntimeException {
    public CouponNotFoundException(String couponId) {
        super("找不到優惠券，ID: " + couponId);
    }
}
