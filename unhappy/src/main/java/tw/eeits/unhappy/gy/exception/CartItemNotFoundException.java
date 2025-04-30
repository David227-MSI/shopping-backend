package tw.eeits.unhappy.gy.exception;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(Integer userId,Integer productId) {
        super("找不到使用者 ID: " + userId + " 的購物車項目（商品 ID: " + productId + "）");
    }
}
