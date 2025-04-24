package tw.eeits.unhappy.gy.domain;
// TODO: 這是 stub 暫時版本，等 teammate 上傳正式版本後刪除

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "coupon_template")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CouponTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "applicable_id")
    private Integer applicableId;

    @Column(name = "applicable_type", length = 10)
    private String applicableType; // ex: "ALL", "PRODUCT", "CATEGORY"

    @Column(name = "min_spend", precision = 15, scale = 2)
    private BigDecimal minSpend;

    @Column(name = "discount_type", length = 10)
    private String discountType; // ex: "FIXED", "PERCENT"

    @Column(name = "discount_value", precision = 15, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "max_discount", precision = 15, scale = 2)
    private BigDecimal maxDiscount;
}
