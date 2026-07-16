package edu.valle.modules.sales.dto.response;

import java.math.BigDecimal;

public record SaleItemResponse(
        Long id,
        Long productVariantId,
        Long productId,
        String productName,
        String size,
        String color,
        String sku,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}
