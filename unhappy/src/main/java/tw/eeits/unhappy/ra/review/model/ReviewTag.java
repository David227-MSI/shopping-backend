package tw.eeits.unhappy.ra.review.model;

public enum ReviewTag {
    FAST("出貨快"),
    QUALITY("品質好"),
    VALUE("CP值高"),
    PACKAGING("包裝完整"),
    MATCHING("描述相符"),
    REPURCHASE("回購意願"),
    SERVICE("客服親切");

    private final String label;

    ReviewTag(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ReviewTag fromLabel(String label) {
        for (ReviewTag tag : values()) {
            if (tag.label.equals(label)) {
                return tag;
            }
        }
        throw new IllegalArgumentException("Unknown label: " + label);
    }
}
