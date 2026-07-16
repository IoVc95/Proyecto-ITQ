package edu.valle.modules.inventory.controller;

import edu.valle.modules.inventory.dto.request.StockAdjustRequest;
import edu.valle.modules.inventory.dto.request.StockInRequest;
import edu.valle.modules.inventory.dto.response.InventoryResponse;
import edu.valle.modules.inventory.dto.response.StockMovementResponse;
import edu.valle.modules.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
@Tag(name = "Inventory", description = "Gestión de inventario y movimientos de stock")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/products/{productId}")
    @Operation(summary = "Consultar inventario por producto", description = "Obtiene el inventario asociado a un producto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventario encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto o inventario no encontrado")
    })
    public InventoryResponse findByProductId(@PathVariable Long productId) {
        return inventoryService.findByProductId(productId);
    }

    @PostMapping("/products/{productId}/stock-in")
    @Operation(summary = "Ingresar stock por producto", description = "Registra un ingreso de stock usando el ID del producto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ingreso de stock registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "422", description = "Regla de negocio incumplida")
    })
    public StockMovementResponse addStockByProductId(
            @PathVariable Long productId,
            @Valid @RequestBody StockInRequest request
    ) {
        return inventoryService.addStockByProductId(productId, request);
    }

    @PostMapping("/stock-in")
    @Operation(summary = "Ingresar stock por código", description = "Registra un ingreso de stock usando el código del producto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ingreso de stock registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "422", description = "Regla de negocio incumplida")
    })
    public StockMovementResponse addStockByCode(
            @RequestParam String code,
            @Valid @RequestBody StockInRequest request
    ) {
        return inventoryService.addStockByCode(code, request);
    }

    @PostMapping("/stock-in/by-barcode")
    @Operation(summary = "Ingresar stock por barcode")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ingreso de stock registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public StockMovementResponse addStockByBarcode(
            @RequestParam String barcode,
            @Valid @RequestBody StockInRequest request
    ) {
        return inventoryService.addStockByBarcode(barcode, request);
    }

    @PutMapping("/products/{productId}/adjust")
    @Operation(summary = "Ajustar stock", description = "Registra un ajuste manual de stock para un producto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ajuste de stock registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "422", description = "Regla de negocio incumplida")
    })
    public StockMovementResponse adjustStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockAdjustRequest request
    ) {
        return inventoryService.adjustStock(productId, request);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Listar productos con bajo stock", description = "Obtiene los productos cuyo stock está por debajo del mínimo definido.")
    @ApiResponse(responseCode = "200", description = "Productos con bajo stock obtenidos correctamente")
    public List<InventoryResponse> findLowStockProducts() {
        return inventoryService.findLowStockProducts();
    }
}
