package tw.eeits.unhappy.ll._others;

public enum OrderStatus {
    PENDING("待付款"),
    PAID("已付款"),
    COMPLETED("已完成"),
    CANCELED("取消");

    private final String displayText;

    OrderStatus(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }
}
