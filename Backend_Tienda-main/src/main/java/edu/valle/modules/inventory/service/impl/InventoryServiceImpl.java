package edu.valle.modules.inventory.service.impl;

import edu.valle.common.enums.StockMovementType;
import edu.valle.exception.BadRequestException;
import edu.valle.exception.ResourceNotFoundException;
import edu.valle.modules.catalog.entity.Product;
import edu.valle.modules.catalog.repository.ProductRepository;
import edu.valle.modules.inventory.dto.request.StockAdjustRequest;
import edu.valle.modules.inventory.dto.request.StockInRequest;
import edu.valle.modules.inventory.dto.response.InventoryResponse;
import edu.valle.modules.inventory.dto.response.StockMovementResponse;
import edu.valle.modules.inventory.entity.Inventory;
import edu.valle.modules.inventory.entity.StockMovement;
import edu.valle.modules.inventory.mapper.InventoryMapper;
import edu.valle.modules.inventory.repository.InventoryRepository;
import edu.valle.modules.inventory.repository.StockMovementRepository;
import edu.valle.modules.inventory.service.InventoryService;
import edu.valle.modules.users.entity.User;
import edu.valle.modules.users.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse findByProductId(Long productId) {
        return inventoryMapper.toResponse(findInventoryByProductId(productId));
    }

    @Override
    @Transactional
    public StockMovementResponse addStockByProductId(Long productId, StockInRequest request) {
        Inventory inventory = findInventoryByProductId(productId);
        User user = findUser(request.userId());
        return inventoryMapper.toMovementResponse(applyStockChange(
                inventory,
                user,
                StockMovementType.IN,
                request.quantity(),
                inventory.getCurrentStock() + request.quantity(),
                request.reason()
        ));
    }

    @Override
    @Transactional
    public StockMovementResponse addStockByBarcode(String barcode, StockInRequest request) {
        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with barcode: " + barcode));
        return addStockByProductId(product.getId(), request);
    }

    @Override
    @Transactional
    public StockMovementResponse addStockByCode(String code, StockInRequest request) {
        return addStockByBarcode(code, request);
    }

    @Override
    @Transactional
    public StockMovementResponse increaseStockFromSupplier(String barcode, int quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El producto existe en proveedor, pero no está registrado en la tienda"));
        Inventory inventory = findInventoryByProductId(product.getId());
        User user = findSupplierPanelUser();

        return inventoryMapper.toMovementResponse(applyStockChange(
                inventory,
                user,
                StockMovementType.IN,
                quantity,
                inventory.getCurrentStock() + quantity,
                "Reabastecimiento desde proveedor SOAP"
        ));
    }

    @Override
    @Transactional
    public StockMovementResponse adjustStock(Long productId, StockAdjustRequest request) {
        Inventory inventory = findInventoryByProductId(productId);
        User user = findUser(request.userId());
        int previousStock = inventory.getCurrentStock();
        int quantity = Math.abs(request.newStock() - previousStock);
        return inventoryMapper.toMovementResponse(applyStockChange(
                inventory,
                user,
                StockMovementType.ADJUSTMENT,
                quantity,
                request.newStock(),
                request.reason()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> findLowStockProducts() {
        return inventoryRepository.findLowStock().stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    private StockMovement applyStockChange(
            Inventory inventory,
            User user,
            StockMovementType movementType,
            Integer quantity,
            Integer newStock,
            String reason
    ) {
        int previousStock = inventory.getCurrentStock();
        inventory.setCurrentStock(newStock);
        inventoryRepository.save(inventory);

        StockMovement movement = new StockMovement();
        movement.setProduct(inventory.getProduct());
        movement.setUser(user);
        movement.setMovementType(movementType);
        movement.setQuantity(quantity);
        movement.setPreviousStock(previousStock);
        movement.setNewStock(newStock);
        movement.setReason(reason);
        return stockMovementRepository.save(movement);
    }

    private Inventory findInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + productId));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    private User findSupplierPanelUser() {
        return userRepository.findFirstByActiveTrueOrderByIdAsc()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No hay un usuario activo para registrar el movimiento de inventario"));
    }
}
