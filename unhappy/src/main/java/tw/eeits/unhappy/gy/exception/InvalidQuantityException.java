package tw.eeits.unhappy.gy.exception;

public class InvalidQuantityException extends RuntimeException {
    public InvalidQuantityException(Integer quantity) {
        super("數量異常 ( 輸入 : " + quantity + ") ");
    }
    public InvalidQuantityException(String message) {
        super(message);
    }
}
