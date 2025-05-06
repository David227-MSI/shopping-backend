package tw.eeits.unhappy.ll.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.eeits.unhappy.eeit198product.entity.Product;
import tw.eeits.unhappy.eeit198product.repository.ProductRepository;
import tw.eeits.unhappy.gy.domain.OrderItem;
import tw.eeits.unhappy.gy.order.repository.OrderItemRepository;
import tw.eeits.unhappy.gy.order.repository.OrderRepository;
import tw.eeits.unhappy.ll.model.Brand;
import tw.eeits.unhappy.ll.model.SalesReport;
import tw.eeits.unhappy.ll.repository.BrandRepository;
import tw.eeits.unhappy.ll.repository.SalesReportRepository;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SalesReportService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final SalesReportRepository salesReportRepository;

    public void generateSalesReport(String reportMonth) {
        LocalDate startDate = LocalDate.parse(reportMonth + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDate = startDate.plusMonths(1);

        List<Integer> paidOrderIds = orderRepository.findPaidOrderIdsBetweenDates(
                startDate.atStartOfDay(), endDate.atStartOfDay());
        if (paidOrderIds.isEmpty()) return;

        List<OrderItem> orderItems = orderItemRepository.findByOrderIdIn(paidOrderIds);
        if (orderItems.isEmpty()) return;

        Set<Integer> productIds = orderItems.stream()
                .map(item -> item.getProduct().getId())
                .collect(Collectors.toSet());
        Map<Integer, Product> productMap = productRepository.findByIdIn(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        Set<Integer> brandIds = productMap.values().stream()
                .map(p -> p.getBrand().getId())
                .collect(Collectors.toSet());
        Map<Integer, Brand> brandMap = brandRepository.findByIdIn(brandIds).stream()
                .collect(Collectors.toMap(Brand::getId, b -> b));

        Integer maxVersion = salesReportRepository.findMaxVersionByMonth(reportMonth);
        int newVersion = (maxVersion == null) ? 1 : maxVersion + 1;

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
                        item.getPriceAtTheTime().multiply(BigDecimal.valueOf(item.getQuantity())));
                return agg;
            });
        }

        List<SalesReport> reports = new ArrayList<>();
        for (Map.Entry<Integer, SalesAggregation> entry : aggregationMap.entrySet()) {
            Integer productId = entry.getKey();
            SalesAggregation agg = entry.getValue();
            Product product = productMap.get(productId);
            Brand brand = brandMap.getOrDefault(product.getBrand().getId(), null);

            reports.add(SalesReport.builder()
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
                    .build());
        }

        salesReportRepository.saveAll(reports);
    }

    @Transactional(readOnly = true)
    public List<SalesReport> getSalesReportsByMonth(String reportMonth) {
        return salesReportRepository.findByReportMonthOrderByVersionDesc(reportMonth);
    }

    public byte[] exportToExcel(String reportMonth, Integer version) {
        List<SalesReport> reports = salesReportRepository
                .findByReportMonthAndVersionOrderByBrandNameAscProductNameAsc(reportMonth, version);
        if (reports.isEmpty()) return null;

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("報表");
            String[] titles = {"品牌", "商品", "平均價格", "銷售數量", "總金額", "產生時間"};
            Row header = sheet.createRow(0);
            for (int i = 0; i < titles.length; i++) header.createCell(i).setCellValue(titles[i]);

            int rowIdx = 1;
            LocalDateTime now = LocalDateTime.now();
            for (SalesReport r : reports) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(r.getBrandName());
                row.createCell(1).setCellValue(r.getProductName());
                row.createCell(2).setCellValue(r.getAveragePrice().toString());
                row.createCell(3).setCellValue(r.getQuantitySold());
                row.createCell(4).setCellValue(r.getTotalAmount().toString());
                row.createCell(5).setCellValue(r.getGeneratedAt().toString());
                r.setIsExported(true);
                r.setExportedAt(now);
            }

            salesReportRepository.saveAll(reports);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("匯出報表失敗", e);
        }
    }

    public byte[] exportToExcelByBrand(String reportMonth, Integer version, Integer brandId) {
        List<SalesReport> reports = salesReportRepository
                .findByReportMonthAndVersionAndBrandIdOrderByProductNameAsc(reportMonth, version, brandId);
        if (reports.isEmpty()) return null;

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("報表");
            String[] titles = {"品牌", "商品", "平均價格", "銷售數量", "總金額", "產生時間"};
            Row header = sheet.createRow(0);
            for (int i = 0; i < titles.length; i++) header.createCell(i).setCellValue(titles[i]);

            int rowIdx = 1;
            LocalDateTime now = LocalDateTime.now();
            for (SalesReport r : reports) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(r.getBrandName());
                row.createCell(1).setCellValue(r.getProductName());
                row.createCell(2).setCellValue(r.getAveragePrice().toString());
                row.createCell(3).setCellValue(r.getQuantitySold());
                row.createCell(4).setCellValue(r.getTotalAmount().toString());
                row.createCell(5).setCellValue(r.getGeneratedAt().toString());
                r.setIsExported(true);
                r.setExportedAt(now);
            }

            salesReportRepository.saveAll(reports);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("匯出品牌報表失敗", e);
        }
    }

    @Transactional(readOnly = true)
    public List<SalesReport> getReportsByBrand(Integer brandId) {
        return salesReportRepository.findByBrandIdOrderByReportMonthDescVersionDesc(brandId);
    }

    @Transactional(readOnly = true)
    public List<SalesReport> getLatestVersionReportsByMonth(String reportMonth) {
        Integer maxVersion = salesReportRepository.findMaxVersionByMonth(reportMonth);
        if (maxVersion == null) return List.of();
        return salesReportRepository.findByReportMonthAndVersionOrderByBrandNameAscProductNameAsc(reportMonth, maxVersion);
    }

    private static class SalesAggregation {
        String productName;
        int quantitySold = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public List<Integer> getBrandIdsByMonth(String reportMonth) {
        //  Option 1:  使用 SalesReportRepository (如果可以有效率地完成)
        //  這假設 SalesReportRepository 可以輕鬆修改以完成此操作。
        //  return salesReportRepository.findDistinctBrandIdsByReportMonth(reportMonth);  //  需要在 SalesReportRepository 中新增

        //  Option 2:  更穩健，但可能效率較低 (如果資料量很大)
        //  取得該月份的所有報表，並提取不重複的品牌 ID。
        List<SalesReport> reports = salesReportRepository.findByReportMonthOrderByVersionDesc(reportMonth);
        return reports.stream()
                .map(SalesReport::getBrandId)
                .distinct()
                .collect(Collectors.toList());
    }

}



