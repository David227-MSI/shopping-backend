package tw.eeits.unhappy.ll.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sales_report")
public class SalesReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "report_month", nullable = false, length = 7, columnDefinition = "CHAR(7)")
    private String reportMonth; // 格式範例：2025-04

    @NotNull
    @Column(nullable = false)
    private Integer version;

    @NotNull
    @Column(name = "brand_id", nullable = false)
    private Integer brandId;

    @NotNull
    @Column(name = "brand_name", nullable = false, length = 100)
    private String brandName;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @NotNull
    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @NotNull
    @Column(name = "average_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal averagePrice;

    @NotNull
    @Column(name = "quantity_sold", nullable = false)
    private Integer quantitySold;

    @NotNull
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @NotNull
    @Column(name = "is_exported", nullable = false)
    private Boolean isExported;

    @Column(name = "exported_at")
    private LocalDateTime exportedAt;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;
}
