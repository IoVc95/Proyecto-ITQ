package edu.valle.modules.inventory.dto.response;

import edu.valle.common.enums.StockMovementType;
import java.time.LocalDateTime;

public record StockMovementResponse(
        Long id,
        Long productId,
        String productName,
        Long userId,
        String username,
        StockMovementType movementType,
        Integer quantity,
        Integer previousStock,
        Integer newStock,
        String reason,
        LocalDateTime createdAt
) {
}