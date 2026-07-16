package edu.valle.modules.inventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StockAdjustRequest(
        @NotNull @Min(0) Integer newStock,
        @NotNull Long userId,
        @Size(max = 500) String reason
) {
}