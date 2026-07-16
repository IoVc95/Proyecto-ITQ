package edu.valle.modules.reports.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyIncomeResponse(
        LocalDate date,
        BigDecimal income
) {
}
