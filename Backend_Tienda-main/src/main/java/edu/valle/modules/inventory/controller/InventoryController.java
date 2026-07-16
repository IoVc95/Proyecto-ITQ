package edu.valle.modules.inventory.controller;
import edu.valle.modules.inventory.dto.request.*; import edu.valle.modules.inventory.dto.response.*; import edu.valle.modules.inventory.service.InventoryService; import jakarta.validation.Valid; import java.util.List; import lombok.RequiredArgsConstructor; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/inventory") @RequiredArgsConstructor @PreAuthorize("hasAnyRole('ADMIN','SELLER')") public class InventoryController {private final InventoryService service;
 @GetMapping("/product-variants/{id}") public InventoryResponse find(@PathVariable Long id){return service.findByProductVariantId(id);}
 @PostMapping("/product-variants/{id}/stock-in") public StockMovementResponse add(@PathVariable Long id,@Valid @RequestBody StockInRequest r){return service.addStock(id,r);}
 @PutMapping("/product-variants/{id}/adjust") public StockMovementResponse adjust(@PathVariable Long id,@Valid @RequestBody StockAdjustRequest r){return service.adjustStock(id,r);}
 @GetMapping("/low-stock") public List<InventoryResponse> low(){return service.findLowStockProducts();}}
