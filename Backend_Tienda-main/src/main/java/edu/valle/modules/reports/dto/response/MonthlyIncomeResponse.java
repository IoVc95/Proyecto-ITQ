package edu.valle.modules.reports.dto.response;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthlyIncomeResponse(
        YearMonth month,
        BigDecimal income
) {
}