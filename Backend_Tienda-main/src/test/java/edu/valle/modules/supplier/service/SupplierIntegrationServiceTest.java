package edu.valle.modules.supplier.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.valle.common.enums.StockMovementType;
import edu.valle.common.enums.UserRole;
import edu.valle.exception.BusinessException;
import edu.valle.exception.ResourceNotFoundException;
import edu.valle.modules.catalog.entity.Product;
import edu.valle.modules.catalog.entity.ProductVariant;
import edu.valle.modules.catalog.repository.ProductVariantRepository;
import edu.valle.modules.inventory.entity.Inventory;
import edu.valle.modules.inventory.entity.StockMovement;
import edu.valle.modules.inventory.repository.InventoryRepository;
import edu.valle.modules.inventory.repository.StockMovementRepository;
import edu.valle.modules.supplier.dto.request.SupplierReplenishmentRequest;
import edu.valle.modules.supplier.dto.response.SupplierReplenishmentResponse;
import edu.valle.modules.supplier.soap.SupplierSoapClient;
import edu.valle.modules.supplier.soap.dto.ReplenishSupplierProductResponse;
import edu.valle.modules.users.entity.User;
import edu.valle.modules.users.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

@ExtendWith(MockitoExtension.class)
class SupplierIntegrationServiceTest {

    @Mock private SupplierSoapClient soapClient;
    @Mock private ProductVariantRepository variants;
    @Mock private InventoryRepository inventories;
    @Mock private StockMovementRepository movements;
    @Mock private UserRepository users;
    @Mock private PlatformTransactionManager transactionManager;
    private SupplierIntegrationService service;

    @BeforeEach
    void setUp() {
        when(transactionManager.getTransaction(any())).thenReturn(mock(TransactionStatus.class));
        service = new SupplierIntegrationService(
                soapClient, variants, inventories, movements, users, transactionManager);
    }

    @Test
    void replenishesStoreAndRecordsAuthenticatedUser() {
        User seller = user(UserRole.SELLER, true);
        ProductVariant variant = variant(true, true);
        Inventory inventory = new Inventory();
        inventory.setProductVariant(variant);
        inventory.setCurrentStock(5);
        StockMovement savedMovement = new StockMovement();
        savedMovement.setId(40L);
        ReplenishSupplierProductResponse supplier = new ReplenishSupplierProductResponse();
        supplier.setSuccess(true);
        supplier.setSku("SKU-1");
        supplier.setAvailableStock(17);

        when(users.findByUsername("seller")).thenReturn(Optional.of(seller));
        when(users.findById(2L)).thenReturn(Optional.of(seller));
        when(variants.findBySkuIgnoreCase("SKU-1")).thenReturn(Optional.of(variant));
        when(variants.findById(7L)).thenReturn(Optional.of(variant));
        when(inventories.existsByProductVariantId(7L)).thenReturn(true);
        when(inventories.findByProductVariantIdForUpdate(7L)).thenReturn(Optional.of(inventory));
        when(soapClient.reserve("SKU-1", 3)).thenReturn(supplier);
        when(movements.save(any())).thenReturn(savedMovement);

        SupplierReplenishmentResponse response = service.replenish(
                "seller", new SupplierReplenishmentRequest("SKU-1", 3));

        assertEquals(8, response.storeNewStock());
        assertEquals(17, response.supplierRemainingStock());
        assertEquals(40L, response.stockMovementId());
        ArgumentCaptor<StockMovement> movement = ArgumentCaptor.forClass(StockMovement.class);
        verify(movements).save(movement.capture());
        assertEquals(seller, movement.getValue().getUser());
        assertEquals(StockMovementType.IN, movement.getValue().getMovementType());
        assertEquals(5, movement.getValue().getPreviousStock());
        assertEquals(8, movement.getValue().getNewStock());
    }

    @Test
    void compensatesSupplierWhenLocalLockedInventoryDisappears() {
        User admin = user(UserRole.ADMIN, true);
        ProductVariant variant = variant(true, true);
        ReplenishSupplierProductResponse supplier = new ReplenishSupplierProductResponse();
        supplier.setSuccess(true);
        supplier.setSku("SKU-1");
        supplier.setAvailableStock(9);

        when(users.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(users.findById(2L)).thenReturn(Optional.of(admin));
        when(variants.findBySkuIgnoreCase("SKU-1")).thenReturn(Optional.of(variant));
        when(variants.findById(7L)).thenReturn(Optional.of(variant));
        when(inventories.existsByProductVariantId(7L)).thenReturn(true);
        when(inventories.findByProductVariantIdForUpdate(7L)).thenReturn(Optional.empty());
        when(soapClient.reserve("SKU-1", 1)).thenReturn(supplier);

        assertThrows(ResourceNotFoundException.class, () -> service.replenish(
                "admin", new SupplierReplenishmentRequest("SKU-1", 1)));

        verify(soapClient).restore("SKU-1", 1);
    }

    @Test
    void rejectsInactiveLocalVariantBeforeCallingSupplier() {
        User seller = user(UserRole.SELLER, true);
        when(users.findByUsername("seller")).thenReturn(Optional.of(seller));
        when(variants.findBySkuIgnoreCase("SKU-1"))
                .thenReturn(Optional.of(variant(false, true)));

        assertThrows(BusinessException.class, () -> service.replenish(
                "seller", new SupplierReplenishmentRequest("SKU-1", 1)));

        verify(soapClient, never()).reserve(any(), org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    void rejectsMissingLocalInventoryBeforeCallingSupplier() {
        User seller = user(UserRole.SELLER, true);
        ProductVariant variant = variant(true, true);
        when(users.findByUsername("seller")).thenReturn(Optional.of(seller));
        when(variants.findBySkuIgnoreCase("SKU-1")).thenReturn(Optional.of(variant));
        when(inventories.existsByProductVariantId(7L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.replenish(
                "seller", new SupplierReplenishmentRequest("SKU-1", 1)));

        verify(soapClient, never()).reserve(any(), org.mockito.ArgumentMatchers.anyInt());
    }

    private User user(UserRole role, boolean active) {
        User user = new User();
        user.setId(2L);
        user.setUsername(role.name().toLowerCase());
        user.setRole(role);
        user.setActive(active);
        return user;
    }

    private ProductVariant variant(boolean active, boolean productActive) {
        Product product = new Product();
        product.setActive(productActive);
        ProductVariant variant = new ProductVariant();
        variant.setId(7L);
        variant.setSku("SKU-1");
        variant.setActive(active);
        variant.setProduct(product);
        return variant;
    }
}
