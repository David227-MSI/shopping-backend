package tw.eeits.unhappy.ll.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ll.model.SalesReport;
import tw.eeits.unhappy.ll.service.SalesReportService;

@RestController
@RequestMapping("/api/admin/sales-reports")
@RequiredArgsConstructor
public class SalesReportController {

    private final SalesReportService salesReportService;

    // 查詢某月份品牌版本報表
    @GetMapping
    public List<SalesReport> getReports(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) Integer brandId,
            @RequestParam(required = false) Integer version) {
        if (month != null && brandId != null && version != null) {
            return salesReportService.getReportsByMonthBrandAndVersion(month, brandId, version);
        }

        if (month != null && brandId != null) {
            return salesReportService.getReportsByMonthAndBrand(month, brandId);
        }

        if (month != null) {
            return salesReportService.getReportsByMonth(month);
        }

        if (brandId != null) {
            return salesReportService.getReportsByBrand(brandId);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "請指定查詢條件");
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportReports(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) Integer brandId,
            @RequestParam(required = false) Integer version) throws IOException {

        Workbook workbook = salesReportService.exportReports(month, brandId, version);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        byte[] excelBytes = out.toByteArray();

        // 取得報表內容（用來取品牌名）
        List<SalesReport> reports = salesReportService.findReportsForExport(month, brandId, version);
        String filename = generateReportFilename(month, brandId, version, reports);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition
                .attachment()
                .filename(filename)
                .build());

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }

    private String generateReportFilename(String month, Integer brandId, Integer version, List<SalesReport> reports) {
        StringBuilder name = new StringBuilder();

        if (month != null) {
            name.append(month); // 例如 2025-04
        }

        String brandName = null;
        if (brandId != null) {
            brandName = !reports.isEmpty() ? reports.get(0).getBrandName() : "brand";

            // ✅ 判斷品牌名稱是否包含非 ASCII 字元（中文、全形符號等）
            boolean containsNonAscii = brandName.chars().anyMatch(c -> c > 127);
            if (containsNonAscii) {
                brandName = "brand";
            }

            name.append("_").append(brandName);
        }

        if (version != null) {
            name.append("_v").append(version);
        }

        name.append("_sales_Report.xlsx");

        return name.toString();
    }

    // 產生報表（全部品牌）
    @PostMapping("/generate")
    public ResponseEntity<List<SalesReport>> generateReport(
            @RequestParam String month,
            @RequestParam(required = false) Integer brandId) {

        List<SalesReport> reports = salesReportService.generateMonthlyReport(month, brandId);

        if (reports.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reports);
    }

}
