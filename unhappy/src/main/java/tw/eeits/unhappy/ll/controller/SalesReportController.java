package tw.eeits.unhappy.ll.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.eeits.unhappy.ll.model.SalesReport;
import tw.eeits.unhappy.ll.service.SalesReportService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sales-reports")
@RequiredArgsConstructor
public class SalesReportController {

    private final SalesReportService salesReportService;

    /**
     * 產生指定月份的報表（全部品牌）
     */
    @PostMapping("/generate")
    public ResponseEntity<String> generateReport(@RequestParam String month) {
        salesReportService.generateSalesReport(month);
        return ResponseEntity.ok("報表產生成功");
    }

    /**
     * 查詢指定月份所有版本的報表
     */
    @GetMapping("/by-month")
    public List<SalesReport> getReportsByMonth(@RequestParam String month) {
        return salesReportService.getSalesReportsByMonth(month);
    }

    /**
     * 匯出某月份某版本報表（全部品牌）
     */
    @GetMapping("/export-all")
    public ResponseEntity<byte[]> exportFullExcel(
            @RequestParam String month,
            @RequestParam Integer version
    ) {
        byte[] file = salesReportService.exportToExcel(month, version);
        if (file == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report_" + month + "_v" + version + ".xlsx")
                .body(file);
    }

    /**
     * 匯出某月份某版本報表（單品牌）
     */
    @GetMapping("/export/brand")
    public ResponseEntity<byte[]> exportExcelByBrand(
            @RequestParam String month,
            @RequestParam Integer version,
            @RequestParam Integer brandId
    ) {
        byte[] file = salesReportService.exportToExcelByBrand(month, version, brandId);
        if (file == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=brand_" + brandId + "_report_" + month + "_v" + version + ".xlsx")
                .body(file);
    }

    /**
     * 查詢某品牌所有報表
     */
    @GetMapping("/brand/{brandId}")
    public List<SalesReport> getReportsByBrand(@PathVariable Integer brandId) {
        return salesReportService.getReportsByBrand(brandId);
    }

    /**
     * 查詢某月份最新版本報表
     */
    @GetMapping("/latest")
    public List<SalesReport> getLatestReports(@RequestParam String month) {
        return salesReportService.getLatestVersionReportsByMonth(month);
    }

    /**
     * 查詢某月份下的所有品牌 ID
     */
    @GetMapping("/month-brands")
    public List<Integer> getBrandIdsByMonth(@RequestParam String month) {
        return salesReportService.getBrandIdsByMonth(month);
    }
}