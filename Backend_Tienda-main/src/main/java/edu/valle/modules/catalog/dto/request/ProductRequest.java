package edu.valle.modules.catalog.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 700) String description,
        @NotBlank @Size(max = 80) String sku,
        @Size(max = 120) String barcode,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal salePrice,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal costPrice,
        Boolean active,
        @NotNull Long categoryId
) {
}
