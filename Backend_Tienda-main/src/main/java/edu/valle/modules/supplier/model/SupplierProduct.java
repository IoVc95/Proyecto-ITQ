package edu.valle.modules.supplier.model;

import java.math.BigDecimal;

public record SupplierProduct(
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
