package tw.eeits.unhappy.ll.service;

import java.util.List;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Cell;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ll.model.SalesReport;
import tw.eeits.unhappy.ll.repository.SalesReportRepository;

@Service
@RequiredArgsConstructor
public class SalesReportServiceImpl implements SalesReportService {

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

}
