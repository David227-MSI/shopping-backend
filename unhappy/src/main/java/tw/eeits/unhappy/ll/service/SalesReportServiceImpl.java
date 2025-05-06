package tw.eeits.unhappy.ll.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.eeit198product.entity.Product;
import tw.eeits.unhappy.gy.domain.OrderItem;
import tw.eeits.unhappy.gy.order.repository.OrderItemRepository;
import tw.eeits.unhappy.gy.order.repository.OrderRepository;
import tw.eeits.unhappy.ll.model.SalesReport;
import tw.eeits.unhappy.ll.repository.SalesReportRepository;

@Service
@RequiredArgsConstructor
public class SalesReportServiceImpl implements SalesReportService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final SalesReportRepository salesReportRepository;

    // 查詢某月份所有品牌報表
    @Override
    public List<SalesReport> getReportsByMonth(String month) {
        // 範例：month = "2025-04"
        return salesReportRepository.findByReportMonth(month);
    }

    // 查詢某品牌所有月份的報表（對應 brandId）
    @Override
    public List<SalesReport> getReportsByBrand(Integer brandId) {
        return salesReportRepository.findByBrandId(brandId);
    }

    // 查詢某品牌在某月份的報表
    @Override
    public List<SalesReport> getReportsByMonthAndBrand(String month, Integer brandId) {
        return salesReportRepository.findByReportMonthAndBrandId(month, brandId);
    }

    // 查詢某月份某品牌某版本
    @Override
    public List<SalesReport> getReportsByMonthBrandAndVersion(String month, Integer brandId, Integer version) {
        return salesReportRepository.findByReportMonthAndBrandIdAndVersion(month, brandId, version);
    }

    // 匯出某月份某版本報表
    @Override
    public Workbook exportReports(String month, Integer brandId, Integer version) {
        List<SalesReport> reports = resolveReportQuery(month, brandId, version);

        // ✅ 將每筆報表標記為已匯出
        LocalDateTime now = LocalDateTime.now();
        for (SalesReport report : reports) {
            report.setIsExported(true);
            report.setExportedAt(now);
        }

        // ✅ 一次儲存回資料庫
        salesReportRepository.saveAll(reports);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sales Report");

        CreationHelper creationHelper = workbook.getCreationHelper();
        CellStyle dateTimeStyle = workbook.createCellStyle();
        short format = creationHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm");
        dateTimeStyle.setDataFormat(format);

        // 建立表頭列
        Row header = sheet.createRow(0);
        String[] headers = {
                "品牌", "品牌ID", "商品", "商品ID",
                "平均價格", "銷售數量", "總金額", "產生時間"
        };
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        // 寫入資料列
        int rowIdx = 1;
        for (SalesReport report : reports) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(report.getBrandName());
            row.createCell(1).setCellValue(report.getBrandId());
            row.createCell(2).setCellValue(report.getProductName());
            row.createCell(3).setCellValue(report.getProductId());
            row.createCell(4).setCellValue(report.getAveragePrice().doubleValue());
            row.createCell(5).setCellValue(report.getQuantitySold());
            row.createCell(6).setCellValue(report.getTotalAmount().doubleValue());

            Cell cell = row.createCell(7);
            if (report.getGeneratedAt() != null) {
                cell.setCellValue(Timestamp.valueOf(report.getGeneratedAt())); // 必須轉為 java.util.Date 或 Timestamp
                cell.setCellStyle(dateTimeStyle);
            }
        }

        return workbook;
    }

    private List<SalesReport> resolveReportQuery(String month, Integer brandId, Integer version) {
        if (month != null && brandId != null && version != null) {
            return getReportsByMonthBrandAndVersion(month, brandId, version);
        } else if (month != null && brandId != null) {
            return getReportsByMonthAndBrand(month, brandId);
        } else if (month != null) {
            return getReportsByMonth(month);
        } else if (brandId != null) {
            return getReportsByBrand(brandId);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "請指定查詢條件");
        }
    }

    // 匯出報表時，查詢報表內容（用來取品牌名）
    @Override
    public List<SalesReport> findReportsForExport(String month, Integer brandId, Integer version) {
        return resolveReportQuery(month, brandId, version);
    }

    // 取得資料 產生報表

    // step1: 取得資料
    private List<OrderItem> getPaidOrderItemsByMonth(String month) {
        // 轉換 yyyy-MM 為該月的起始與結束時間
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        // 查詢付款成功的訂單 ID
        List<Integer> paidOrderIds = orderRepository.findPaidOrderIdsBetweenDates(start, end);

        // 如果沒有符合的訂單，直接回傳空清單
        if (paidOrderIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 查詢對應的訂單明細
        return orderItemRepository.findByOrderIdIn(paidOrderIds);
    }

    // step2 將訂單明細 (OrderItem) 分組並彙整為 SalesReport 物件
    private List<SalesReport> groupToSalesReport(String reportMonth, List<OrderItem> items) {
        Map<Integer, List<OrderItem>> grouped = items.stream()
                .collect(Collectors.groupingBy(item -> item.getProduct().getId()));

        List<SalesReport> reports = new ArrayList<>();

        for (Map.Entry<Integer, List<OrderItem>> entry : grouped.entrySet()) {
            Integer productId = entry.getKey();
            List<OrderItem> itemGroup = entry.getValue();

            // 只取第一筆商品資訊（都一樣）
            Product product = itemGroup.get(0).getProduct();

            int totalQty = itemGroup.stream().mapToInt(OrderItem::getQuantity).sum();
            BigDecimal totalAmount = itemGroup.stream()
                    .map(item -> item.getPriceAtTheTime().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal averagePrice = totalQty == 0
                    ? BigDecimal.ZERO
                    : totalAmount.divide(BigDecimal.valueOf(totalQty), 2, RoundingMode.HALF_UP);

            SalesReport report = SalesReport.builder()
                    .reportMonth(reportMonth)
                    .brandId(product.getBrand().getId())
                    .brandName(product.getBrand().getName())
                    .productId(product.getId())
                    .productName(product.getName())
                    .averagePrice(averagePrice)
                    .quantitySold(totalQty)
                    .totalAmount(totalAmount)
                    .isExported(false)
                    .generatedAt(LocalDateTime.now())
                    .version(0) // 下一步才會補上
                    .build();

            reports.add(report);
        }

        return reports;
    }

    // step3 需要檢查該 month + brandId 已有的最大 version，然後 +1
    private int getNextReportVersion(String month, Integer brandId) {
        Integer maxVersion = salesReportRepository.findMaxVersionByMonthAndBrand(month, brandId);
        return maxVersion == null ? 1 : maxVersion + 1;
    }

    // step4 產出報表
    @Override
    public List<SalesReport> generateMonthlyReport(String month, @Nullable Integer brandId) {
        // 取得訂單明細
        List<OrderItem> items = getPaidOrderItemsByMonth(month);

        // 如果指定 brandId，先過濾出該品牌商品
        if (brandId != null) {
            items = items.stream()
                    .filter(item -> item.getProduct().getBrand().getId().equals(brandId))
                    .toList();
        }

        if (items.isEmpty()) {
            return List.of(); // 無資料，不做任何處理
        }

        // 彙整成報表格式（尚未設定 version）
        List<SalesReport> reports = groupToSalesReport(month, items);

        // 依照 brandId 計算 version（一組一個）
        Map<Integer, Integer> versionMap = new HashMap<>();
        for (SalesReport report : reports) {
            Integer bId = report.getBrandId();
            if (!versionMap.containsKey(bId)) {
                int nextVersion = getNextReportVersion(month, bId);
                versionMap.put(bId, nextVersion);
            }
            report.setVersion(versionMap.get(bId));
        }

        // 寫入資料庫
        return salesReportRepository.saveAll(reports);
    }

}
