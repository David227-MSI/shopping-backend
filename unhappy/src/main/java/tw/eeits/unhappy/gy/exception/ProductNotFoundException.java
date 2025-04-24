package tw.eeits.unhappy.gy.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Integer id) {
        super("找不到商品 id : " + id);
    }

    public ProductNotFoundException(String name) {
        super("找不到商品 : " + name);
    }

    public ProductNotFoundException(Integer id, String name) {
        super("找不到商品 : " + name + "id : " + id);
    }
}
