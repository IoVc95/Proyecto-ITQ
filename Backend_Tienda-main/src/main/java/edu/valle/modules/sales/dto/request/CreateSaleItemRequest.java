package edu.valle.modules.sales.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateSaleItemRequest(
        @NotNull Long productId,
        @NotNull @Positive Integer quantity
) {
}