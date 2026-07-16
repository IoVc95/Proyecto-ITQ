package edu.valle.modules.reports.dto.response;

import java.time.YearMonth;

public record MonthlySalesTotalResponse(
        YearMonth month,
        Long totalSales
) {
}