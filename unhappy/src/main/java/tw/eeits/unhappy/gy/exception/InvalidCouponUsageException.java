package tw.eeits.unhappy.gy.exception;

// 不符合優惠捲標準

public class InvalidCouponUsageException extends RuntimeException {
    public InvalidCouponUsageException(String reason) {
        super("無法使用： " + reason);
    }
}
