package edu.valle.modules.inventory.dto.response;
public record InventoryResponse(Long id,Long productVariantId,Long productId,String productName,String size,String color,String sku,Integer currentStock,Integer minStock,Integer maxStock) {}
