package tw.eeits.unhappy.ll.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tw.eeits.unhappy.ll.model.SalesReport;

public interface SalesReportRepository
		extends JpaRepository<SalesReport, Integer> {
				@Query("SELECT MAX(s.version) FROM SalesReport s WHERE s.reportMonth = :reportMonth")
    Integer findMaxVersionByMonth(@Param("reportMonth") String reportMonth);

	List<SalesReport> findByReportMonthOrderByVersionDesc(String reportMonth);

}