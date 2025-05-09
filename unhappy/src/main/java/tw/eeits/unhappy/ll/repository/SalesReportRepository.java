package tw.eeits.unhappy.ll.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tw.eeits.unhappy.ll.dto.SalesReportSummaryDto;
import tw.eeits.unhappy.ll.model.SalesReport;

public interface SalesReportRepository
    extends JpaRepository<SalesReport, Integer> {

  // 查詢某月份所有品牌報表
  List<SalesReport> findByReportMonth(String reportMonth);

  // 查詢某品牌所有月份的報表（對應 brandId）
  List<SalesReport> findByBrandId(Integer brandId);

  // 查詢某品牌在某月份的報表
  List<SalesReport> findByReportMonthAndBrandId(String reportMonth, Integer brandId);

  // 查詢某月份某品牌某版本
  List<SalesReport> findByReportMonthAndBrandIdAndVersion(String reportMonth, Integer brandId, Integer version);

  // 根據報表的 reportMonth + brandId 自動取得下一個 version。
  @Query("SELECT MAX(sr.version) FROM SalesReport sr WHERE sr.reportMonth = :month AND sr.brandId = :brandId")
  Integer findMaxVersionByMonthAndBrand(@Param("month") String month, @Param("brandId") Integer brandId);

  // 複數產品只顯示一張報表紀錄
  @Query(value = """
      SELECT
          brand_id,
          brand_name,
          report_month,
          version,
          CAST(MAX(CAST(is_exported AS INT)) AS BIT) AS is_exported,
          MAX(exported_at) AS exported_at
      FROM sales_report
      WHERE (:month IS NULL OR report_month = :month)
        AND (:brandId IS NULL OR brand_id = :brandId)
      GROUP BY brand_id, brand_name, report_month, version
      ORDER BY brand_id, report_month, version
      """, nativeQuery = true)
  List<Object[]> findReportSummariesNative(
      @Param("month") String month,
      @Param("brandId") Integer brandId);

}
