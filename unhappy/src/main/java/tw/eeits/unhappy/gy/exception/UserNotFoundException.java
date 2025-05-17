package tw.eeits.unhappy.gy.exception;

public class UserNotFoundException extends RuntimeException {

    private final Integer userId;

    public UserNotFoundException(Integer userId) {
        super("找不到使用者ID : " + userId);
        this.userId = userId;
    }
    public UserNotFoundException(String message) {
        super(message);
        this.userId = null;
    }
    public Integer getUserId() {
        return userId;
    }
}
