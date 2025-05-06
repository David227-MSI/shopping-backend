package tw.eeits.unhappy.ll.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tw.eeits.unhappy.ll.model.SalesReport;

public interface SalesReportRepository
		extends JpaRepository<SalesReport, Integer> {

    //查詢某月份所有品牌報表
    List<SalesReport> findByReportMonth(String reportMonth);


    //查詢某品牌所有月份的報表（對應 brandId）
    List<SalesReport> findByBrandId(Integer brandId);


    //查詢某品牌在某月份的報表
    List<SalesReport> findByReportMonthAndBrandId(String reportMonth, Integer brandId);


    // 查詢某月份某品牌某版本
    List<SalesReport> findByReportMonthAndBrandIdAndVersion(String reportMonth, Integer brandId, Integer version);

    
    // // 查詢某月份的最大版本號
    // @Query("SELECT MAX(s.version) FROM SalesReport s WHERE s.reportMonth = :reportMonth")
    // Integer findMaxVersionByMonth(@Param("reportMonth") String reportMonth);

    // // 查詢某月份所有版本（最新的在前）
    // List<SalesReport> findByReportMonthOrderByVersionDesc(String reportMonth);

    // // 匯出某月份、某版本的完整報表
    // List<SalesReport> findByReportMonthAndVersionOrderByBrandNameAscProductNameAsc(
    //     String reportMonth, Integer version
    // );

    // // 匯出某品牌、某月份、某版本的報表（排序依商品）
    // List<SalesReport> findByReportMonthAndVersionAndBrandIdOrderByProductNameAsc(
    //     String reportMonth, Integer version, Integer brandId
    // );

    // // 查詢某品牌的歷史報表（所有月份與版本）
    // List<SalesReport> findByBrandIdOrderByReportMonthDescVersionDesc(Integer brandId);

    // // 查詢某月份的最新版本報表
    // @Query("""
    //     SELECT s FROM SalesReport s
    //     WHERE s.reportMonth = :month AND s.version = (
    //         SELECT MAX(s2.version) FROM SalesReport s2 WHERE s2.reportMonth = :month
    //     )
    //     ORDER BY s.brandName ASC, s.productName ASC
    // """)
    // List<SalesReport> findLatestVersionByMonth(@Param("month") String month);
}
