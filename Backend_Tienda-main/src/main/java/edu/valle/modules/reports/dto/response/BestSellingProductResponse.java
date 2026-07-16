package edu.valle.modules.reports.dto.response;

import java.math.BigDecimal;

public record BestSellingProductResponse(
        Long productId,
        String productName,
        Long quantitySold,
        BigDecimal revenue
) {
}
