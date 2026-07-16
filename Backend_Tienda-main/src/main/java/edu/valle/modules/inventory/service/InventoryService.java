package edu.valle.modules.inventory.service;
import edu.valle.modules.inventory.dto.request.*; import edu.valle.modules.inventory.dto.response.*; import java.util.List;
public interface InventoryService {InventoryResponse findByProductVariantId(Long id);StockMovementResponse addStock(Long id,StockInRequest r);StockMovementResponse adjustStock(Long id,StockAdjustRequest r);List<InventoryResponse> findLowStockProducts();}
