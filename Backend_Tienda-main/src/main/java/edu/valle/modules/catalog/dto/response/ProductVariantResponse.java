package edu.valle.modules.catalog.dto.response;
import java.time.LocalDateTime;
public record ProductVariantResponse(Long id,Long productId,String productName,String size,String color,String sku,
 Boolean active,LocalDateTime createdAt,LocalDateTime updatedAt) {}
