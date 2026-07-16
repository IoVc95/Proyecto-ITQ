package edu.valle.modules.catalog.dto.request;
import jakarta.validation.constraints.*;
public record ProductVariantRequest(@NotNull Long productId,@NotBlank @Size(max=30) String size,
 @NotBlank @Size(max=50) String color,@NotBlank @Size(max=80) String sku,Boolean active) {}
