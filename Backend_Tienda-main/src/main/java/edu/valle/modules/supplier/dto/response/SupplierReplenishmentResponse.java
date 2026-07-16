package edu.valle.modules.supplier.dto.response;

public record SupplierReplenishmentResponse(
        String sku,
        int transferredQuantity,
        int supplierRemainingStock,
        int storeNewStock,
        Long productVariantId,
        Long stockMovementId
) {
}
