package edu.valle.modules.reports.dto.response;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthlySalesResponse(
        YearMonth month,
        Long totalSales,
        BigDecimal salesAmount
) {
}
