package tw.eeits.unhappy.ll.dto;

import java.time.LocalDateTime;

public record SalesReportSummaryDto(
        Integer brandId,
        String brandName,
        String reportMonth,
        Integer version,
        Boolean isExported,
        LocalDateTime exportedAt) {
}
