package edu.valle.modules.inventory.service;

import edu.valle.modules.inventory.dto.request.StockAdjustRequest;
import edu.valle.modules.inventory.dto.request.StockInRequest;
import edu.valle.modules.inventory.dto.response.InventoryResponse;
import edu.valle.modules.inventory.dto.response.StockMovementResponse;
import java.util.List;

public interface InventoryService {

    InventoryResponse findByProductId(Long productId);

    StockMovementResponse addStockByProductId(Long productId, StockInRequest request);

    StockMovementResponse addStockByBarcode(String barcode, StockInRequest request);

    StockMovementResponse addStockByCode(String code, StockInRequest request);

    StockMovementResponse increaseStockFromSupplier(String barcode, int quantity);

    StockMovementResponse adjustStock(Long productId, StockAdjustRequest request);

    List<InventoryResponse> findLowStockProducts();
}
