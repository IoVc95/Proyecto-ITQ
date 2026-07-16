package edu.valle.modules.inventory.mapper;
import edu.valle.modules.inventory.dto.response.*; import edu.valle.modules.inventory.entity.*; import org.mapstruct.*;
@Mapper(componentModel="spring") public interface InventoryMapper {
 @Mapping(target="productVariantId",source="productVariant.id") @Mapping(target="productId",source="productVariant.product.id") @Mapping(target="productName",source="productVariant.product.name") @Mapping(target="size",source="productVariant.size") @Mapping(target="color",source="productVariant.color") @Mapping(target="sku",source="productVariant.sku") InventoryResponse toResponse(Inventory i);
 @Mapping(target="productVariantId",source="productVariant.id") @Mapping(target="productId",source="productVariant.product.id") @Mapping(target="productName",source="productVariant.product.name") @Mapping(target="size",source="productVariant.size") @Mapping(target="color",source="productVariant.color") @Mapping(target="sku",source="productVariant.sku") @Mapping(target="userId",source="user.id") @Mapping(target="username",source="user.username") StockMovementResponse toMovementResponse(StockMovement m);
}
