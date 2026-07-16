package edu.valle.modules.reports.dto.response;

import java.math.BigDecimal;

public record TopProductResponse(
        Long productId,
        String productName,
        Long quantitySold,
        BigDecimal totalRevenue
) {
}