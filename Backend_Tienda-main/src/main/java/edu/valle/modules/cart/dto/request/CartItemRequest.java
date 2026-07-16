package edu.valle.modules.cart.dto.request;
import jakarta.validation.constraints.*;
public record CartItemRequest(@NotNull Long productVariantId,@NotNull @Positive Integer quantity) {}
