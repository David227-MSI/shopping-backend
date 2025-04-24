package tw.eeits.unhappy.ra.review.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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

    /* ----- 給 Jackson 序列化時用 ----- */
    @JsonValue                 // ➜ to JSON 時輸出中文
    public String toJson() {
        return label;
    }

    /* ----- 給 Jackson 反序列化時用 ----- */
    @JsonCreator               // ➜ 從 JSON 讀中文 → Enum
    public static ReviewTag fromJson(String label) {
        return fromLabel(label);   // 你原本就寫好的方法
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
