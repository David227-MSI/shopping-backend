package tw.eeits.unhappy.gy.exception;

public class EmptyCartException extends RuntimeException {
    public EmptyCartException(Integer userId) {
        super("使用者 ID : " + userId + " 的購物車為空");
    }
}
