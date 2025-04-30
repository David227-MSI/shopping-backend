package tw.eeits.unhappy.ll.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.eeit198product.entity.Product;
import tw.eeits.unhappy.eeit198product.repository.ProductRepository;
import tw.eeits.unhappy.gy.domain.OrderItem;
import tw.eeits.unhappy.gy.order.repository.OrderItemRepository;
import tw.eeits.unhappy.gy.order.repository.OrderRepository;
import tw.eeits.unhappy.ll.model.Brand;
import tw.eeits.unhappy.ll.model.SalesReport;
import tw.eeits.unhappy.ll.repository.BrandRepository;
import tw.eeits.unhappy.ll.repository.SalesReportRepository;

@Service
@RequiredArgsConstructor
@Transactional // 一定要加，避免 Lazy loading 爆錯
public class SalesReportService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final SalesReportRepository salesReportRepository;

    /**
     * 產生指定月份的銷售報表
     * @param reportMonth 格式：yyyy-MM，例如 "2025-04"
     */
    public void generateSalesReport(String reportMonth) {
        // 1. 計算起訖時間
        LocalDate startDate = LocalDate.parse(reportMonth + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDate = startDate.plusMonths(1);

        // 2. 查詢該月份付款成功 (PAID) 的訂單ID
        List<Integer> paidOrderIds = orderRepository.findPaidOrderIdsBetweenDates(
                startDate.atStartOfDay(), endDate.atStartOfDay()
        );
        if (paidOrderIds.isEmpty()) {
            return; // 沒有資料就直接跳出
        }

        // 3. 查詢訂單項目 (OrderItems)
        List<OrderItem> orderItems = orderItemRepository.findByOrderIdIn(paidOrderIds);
        if (orderItems.isEmpty()) {
            return; // 沒有商品就直接跳出
        }

        // 4. 整理所有 Product IDs
        Set<Integer> productIds = orderItems.stream()
                .map(orderItem -> orderItem.getProduct().getId())
                .collect(Collectors.toSet());

        // 5. 查詢商品資料 (Products)
        Map<Integer, Product> productMap = productRepository.findByIdIn(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 6. 查詢品牌資料 (Brands)
        Set<Integer> brandIds = productMap.values().stream()
                .map(product -> product.getBrand().getId())
                .collect(Collectors.toSet());

        Map<Integer, Brand> brandMap = brandRepository.findByIdIn(brandIds).stream()
                .collect(Collectors.toMap(Brand::getId, b -> b));

        // 7. 查詢當月最高的 version
        Integer maxVersion = salesReportRepository.findMaxVersionByMonth(reportMonth);
        int newVersion = (maxVersion == null) ? 1 : maxVersion + 1;

        // 8. 整理彙總資料
        Map<Integer, SalesAggregation> aggregationMap = new HashMap<>();
        for (OrderItem item : orderItems) {
            Integer productId = item.getProduct().getId();
            aggregationMap.compute(productId, (key, agg) -> {
                if (agg == null) {
                    agg = new SalesAggregation();
                    agg.productName = item.getProductNameAtTheTime();
                }
                agg.quantitySold += item.getQuantity();
                agg.totalAmount = agg.totalAmount.add(
                        item.getPriceAtTheTime().multiply(BigDecimal.valueOf(item.getQuantity()))
                );
                return agg;
            });
        }

        // 9. 產生 sales_report 資料
        List<SalesReport> reports = new ArrayList<>();
        for (Map.Entry<Integer, SalesAggregation> entry : aggregationMap.entrySet()) {
            Integer productId = entry.getKey();
            SalesAggregation agg = entry.getValue();

            Product product = productMap.get(productId);
            Brand brand = brandMap.getOrDefault(product.getBrand().getId(), null);

            SalesReport report = SalesReport.builder()
                    .reportMonth(reportMonth)
                    .version(newVersion)
                    .brandId(product.getBrand().getId())
                    .brandName(brand != null ? brand.getName() : "")
                    .productId(productId)
                    .productName(agg.productName)
                    .averagePrice(agg.totalAmount.divide(BigDecimal.valueOf(agg.quantitySold), 2, RoundingMode.HALF_UP))
                    .quantitySold(agg.quantitySold)
                    .totalAmount(agg.totalAmount)
                    .isExported(false)
                    .generatedAt(LocalDateTime.now())
                    .build();

            reports.add(report);
        }

        // 10. 批次存進資料庫
        salesReportRepository.saveAll(reports);
    }

    /**
     * 彙總用的小結構
     */
    private static class SalesAggregation {
        String productName;
        int quantitySold = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
    }

    /**
 * 查詢某個月份的銷售報表（所有版本）
 */
@Transactional(readOnly = true)
public List<SalesReport> getSalesReportsByMonth(String reportMonth) {
    return salesReportRepository.findByReportMonthOrderByVersionDesc(reportMonth);
}


}
