package tw.eeits.unhappy.ll.model;

public enum BrandStatus {
    ACTIVE("啟用"),
    INACTIVE("停用"),
    WARNING("警告");

    private final String label;

    BrandStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}