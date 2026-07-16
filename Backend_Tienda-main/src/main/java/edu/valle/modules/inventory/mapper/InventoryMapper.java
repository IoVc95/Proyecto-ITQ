package edu.valle.modules.inventory.mapper;

import edu.valle.modules.inventory.dto.response.InventoryResponse;
import edu.valle.modules.inventory.dto.response.StockMovementResponse;
import edu.valle.modules.inventory.entity.Inventory;
import edu.valle.modules.inventory.entity.StockMovement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "sku", source = "product.sku")
    @Mapping(target = "barcode", source = "product.barcode")
    InventoryResponse toResponse(Inventory inventory);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    StockMovementResponse toMovementResponse(StockMovement movement);
}
