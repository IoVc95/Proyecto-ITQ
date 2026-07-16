package edu.valle.modules.reports.dto.response;

import java.math.BigDecimal;

public record UserSalesResponse(
        Long userId,
        String username,
        String fullName,
        Long totalSales,
        BigDecimal salesAmount
) {
}
