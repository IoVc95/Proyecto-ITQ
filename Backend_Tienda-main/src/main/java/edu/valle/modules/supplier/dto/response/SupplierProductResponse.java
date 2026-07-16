package edu.valle.modules.supplier.dto.response;

import java.math.BigDecimal;

public record SupplierProductResponse(
        Long id,
        String productName,
        String size,
        String color,
        String sku,
        BigDecimal price,
        Integer availableStock,
        Boolean active
) {
}
