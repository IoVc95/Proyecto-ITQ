package edu.valle.modules.reports.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IncomeRangeResponse(
        LocalDateTime from,
        LocalDateTime to,
        BigDecimal totalIncome
) {
}
