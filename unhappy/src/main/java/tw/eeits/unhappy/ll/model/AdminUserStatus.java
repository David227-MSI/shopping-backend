package tw.eeits.unhappy.ll.model;

public enum AdminUserStatus {
    ACTIVE("啟用"),
    INACTIVE("停用"),
    SUSPENDED("暫停"),
    PENDING("待啟用");

    private final String label;

    AdminUserStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}