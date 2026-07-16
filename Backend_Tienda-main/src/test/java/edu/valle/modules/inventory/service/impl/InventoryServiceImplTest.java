package edu.valle.modules.inventory.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.valle.exception.ResourceNotFoundException;
import edu.valle.modules.catalog.entity.Product;
import edu.valle.modules.catalog.repository.ProductRepository;
import edu.valle.modules.inventory.dto.request.StockInRequest;
import edu.valle.modules.inventory.dto.response.StockMovementResponse;
import edu.valle.modules.inventory.entity.Inventory;
import edu.valle.modules.inventory.entity.StockMovement;
import edu.valle.modules.inventory.mapper.InventoryMapper;
import edu.valle.modules.inventory.repository.InventoryRepository;
import edu.valle.modules.inventory.repository.StockMovementRepository;
import edu.valle.modules.users.entity.User;
import edu.valle.modules.users.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InventoryMapper inventoryMapper;

    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryServiceImpl(
                inventoryRepository,
                stockMovementRepository,
                productRepository,
                userRepository,
                inventoryMapper
        );
    }

    @Test
    void stockInByBarcodeUpdatesInventory() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product");
        product.setBarcode("7501234567890");
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setCurrentStock(5);
        User user = new User();
        user.setId(2L);
        StockInRequest request = new StockInRequest(3, 2L, "Purchase");
        StockMovement savedMovement = new StockMovement();
        StockMovementResponse response = org.mockito.Mockito.mock(StockMovementResponse.class);

        when(productRepository.findByBarcode(product.getBarcode())).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(stockMovementRepository.save(org.mockito.ArgumentMatchers.any(StockMovement.class)))
                .thenReturn(savedMovement);
        when(inventoryMapper.toMovementResponse(savedMovement)).thenReturn(response);

        inventoryService.addStockByBarcode(product.getBarcode(), request);

        assertEquals(8, inventory.getCurrentStock());
        verify(productRepository).findByBarcode(product.getBarcode());
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void missingBarcodeReturnsNotFound() {
        when(productRepository.findByBarcode("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inventoryService.addStockByBarcode(
                "missing",
                new StockInRequest(1, 2L, null)
        ));
    }

    @Test
    void legacyCodeStockInUsesBarcodeOnly() {
        when(productRepository.findByBarcode("legacy-code")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inventoryService.addStockByCode(
                "legacy-code",
                new StockInRequest(1, 2L, null)
        ));
        verify(productRepository).findByBarcode("legacy-code");
    }
}
