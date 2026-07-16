package edu.valle.modules.catalog.dto.request;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record ProductRequest(
        @NotBlank @Size(max=150) String name,
        @Size(max=700) String description,
        @NotNull @DecimalMin(value="0.0", inclusive=false) BigDecimal salePrice,
        @NotNull @DecimalMin(value="0.0", inclusive=true) BigDecimal costPrice,
        Boolean active,
        @NotNull Long categoryId) {}
