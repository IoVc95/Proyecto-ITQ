package edu.valle.modules.inventory.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record StockInRequest(
        @NotNull @Positive Integer quantity,
        @NotNull Long userId,
        @Size(max = 500) String reason
) {
}