package edu.valle.modules.supplier.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record SupplierReplenishmentRequest(
        @NotBlank(message = "SKU is required") String sku,
        @Positive(message = "Quantity must be greater than zero") int quantity
) {
}
