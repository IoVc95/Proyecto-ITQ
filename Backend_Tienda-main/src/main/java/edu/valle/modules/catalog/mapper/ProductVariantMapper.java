package edu.valle.modules.catalog.mapper;
import edu.valle.modules.catalog.dto.request.ProductVariantRequest;
import edu.valle.modules.catalog.dto.response.ProductVariantResponse;
import edu.valle.modules.catalog.entity.ProductVariant;
import org.mapstruct.*;
@Mapper(componentModel="spring",nullValuePropertyMappingStrategy=NullValuePropertyMappingStrategy.IGNORE)
public interface ProductVariantMapper {
 @Mapping(target="product",ignore=true) @Mapping(target="id",ignore=true) @Mapping(target="createdAt",ignore=true) @Mapping(target="updatedAt",ignore=true) ProductVariant toEntity(ProductVariantRequest r);
 @Mapping(target="productId",source="product.id") @Mapping(target="productName",source="product.name") ProductVariantResponse toResponse(ProductVariant v);
 @Mapping(target="product",ignore=true) @Mapping(target="id",ignore=true) @Mapping(target="createdAt",ignore=true) @Mapping(target="updatedAt",ignore=true) void update(ProductVariantRequest r,@MappingTarget ProductVariant v);
}
