package edu.valle.modules.store.dto.response;
import java.math.BigDecimal; import java.util.List;
public record StoreProductResponse(Long id,String name,String description,BigDecimal salePrice,Long categoryId,String categoryName,List<StoreVariantResponse> variants) {}
