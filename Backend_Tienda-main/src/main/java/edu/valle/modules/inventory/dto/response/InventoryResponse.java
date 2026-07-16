package edu.valle.modules.inventory.dto.response;

public record InventoryResponse(
        Long id,
        Long productId,
        String productName,
        String sku,
        String barcode,
        Integer currentStock,
        Integer minStock,
        Integer maxStock
) {
}
