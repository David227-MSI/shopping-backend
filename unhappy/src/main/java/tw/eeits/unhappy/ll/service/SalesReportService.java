package tw.eeits.unhappy.ll.service;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.lang.Nullable;

import tw.eeits.unhappy.ll.model.SalesReport;

public interface SalesReportService {

    // 查詢某月份所有品牌報表
    List<SalesReport> getReportsByMonth(String month);

    // 查詢某品牌所有月份的報表（對應 brandId）
    List<SalesReport> getReportsByBrand(Integer brandId);

    // 查詢某品牌在某月份的報表
    List<SalesReport> getReportsByMonthAndBrand(String month, Integer brandId);

    // 查詢某月份某品牌某版本
    List<SalesReport> getReportsByMonthBrandAndVersion(String month, Integer brandId, Integer version);

    // 匯出某月份某版本報表
    Workbook exportReports(String month, Integer brandId, Integer version);

    // 取得資料（取品牌名命名）
    List<SalesReport> findReportsForExport(String month, Integer brandId, Integer version);

    // 產出月報表
    public List<SalesReport> generateMonthlyReport(String month, @Nullable Integer brandId);

}
