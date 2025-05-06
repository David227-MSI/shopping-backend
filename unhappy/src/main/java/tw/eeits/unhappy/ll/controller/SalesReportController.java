package tw.eeits.unhappy.ll.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ll.model.SalesReport;
import tw.eeits.unhappy.ll.service.SalesReportService;

@RestController
@RequestMapping("/api/sales-report")
@RequiredArgsConstructor
public class SalesReportController {

    private final SalesReportService salesReportService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateReport(@RequestParam String reportMonth) {
        salesReportService.generateSalesReport(reportMonth);
        return ResponseEntity.ok("銷售報表已產生：" + reportMonth);
    }

    /**
     * 查詢某個月份的所有銷售報表資料
     */
    @GetMapping("/list")
    public ResponseEntity<List<SalesReport>> listSalesReports(@RequestParam String reportMonth) {
        List<SalesReport> reports = salesReportService.getSalesReportsByMonth(reportMonth);
        return ResponseEntity.ok(reports);
    }



    // 匯出報表


@GetMapping("/export")
public ResponseEntity<byte[]> exportReport(
        @RequestParam String reportMonth,
        @RequestParam Integer version) {

    byte[] file = salesReportService.exportToExcel(reportMonth, version);
    if (file == null) {
        return ResponseEntity.notFound().build();
    }

    String filename = String.format("sales-report-%s-v%d.xlsx", reportMonth, version);

    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .body(file);
}









}


