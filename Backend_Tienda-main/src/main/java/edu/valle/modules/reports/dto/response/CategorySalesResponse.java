package edu.valle.modules.reports.dto.response;

import java.math.BigDecimal;

public record CategorySalesResponse(
        Long categoryId,
        String categoryName,
        Long quantitySold,
        BigDecimal salesAmount
) {
}
