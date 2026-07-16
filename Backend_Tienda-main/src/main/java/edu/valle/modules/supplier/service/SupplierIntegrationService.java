package edu.valle.modules.supplier.service;

import edu.valle.common.enums.StockMovementType;
import edu.valle.common.enums.UserRole;
import edu.valle.exception.BusinessException;
import edu.valle.exception.ResourceNotFoundException;
import edu.valle.modules.catalog.entity.ProductVariant;
import edu.valle.modules.catalog.repository.ProductVariantRepository;
import edu.valle.modules.inventory.entity.Inventory;
import edu.valle.modules.inventory.entity.StockMovement;
import edu.valle.modules.inventory.repository.InventoryRepository;
import edu.valle.modules.inventory.repository.StockMovementRepository;
import edu.valle.modules.supplier.dto.request.SupplierReplenishmentRequest;
import edu.valle.modules.supplier.dto.response.SupplierProductResponse;
import edu.valle.modules.supplier.dto.response.SupplierReplenishmentResponse;
import edu.valle.modules.supplier.soap.SupplierSoapClient;
import edu.valle.modules.supplier.soap.dto.SupplierProductSoapResponse;
import edu.valle.modules.users.entity.User;
import edu.valle.modules.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class SupplierIntegrationService {

    private static final String MOVEMENT_REASON = "Supplier SOAP replenishment";

    private final SupplierSoapClient soapClient;
    private final ProductVariantRepository variantRepository;
    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository movementRepository;
    private final UserRepository userRepository;
    private final TransactionTemplate mainTransaction;

    public SupplierIntegrationService(
            SupplierSoapClient soapClient,
            ProductVariantRepository variantRepository,
            InventoryRepository inventoryRepository,
            StockMovementRepository movementRepository,
            UserRepository userRepository,
            @Qualifier("transactionManager") PlatformTransactionManager transactionManager) {
        this.soapClient = soapClient;
        this.variantRepository = variantRepository;
        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
        this.userRepository = userRepository;
        this.mainTransaction = new TransactionTemplate(transactionManager);
    }

    public SupplierProductResponse getSupplierProduct(String sku) {
        return toProductResponse(soapClient.getBySku(sku));
    }

    public SupplierReplenishmentResponse replenish(
            String username, SupplierReplenishmentRequest request) {
        Preflight preflight = mainTransaction.execute(status -> validatePreflight(username, request.sku()));
        if (preflight == null) {
            throw new BusinessException("Could not validate replenishment");
        }

        SupplierProductSoapResponse supplier = soapClient.reserve(request.sku(), request.quantity());
        try {
            LocalResult local = mainTransaction.execute(status ->
                    updateLocalInventory(preflight, request.quantity()));
            if (local == null) {
                throw new BusinessException("Could not update store inventory");
            }
            return new SupplierReplenishmentResponse(
                    supplier.getSku(),
                    request.quantity(),
                    supplier.getAvailableStock(),
                    local.newStock(),
                    preflight.variantId(),
                    local.movementId()
            );
        } catch (RuntimeException localFailure) {
            compensate(request, localFailure);
            throw localFailure;
        }
    }

    private Preflight validatePreflight(String username, String sku) {
        User user = findAuthorizedUser(username);
        ProductVariant variant = findUsableVariant(sku);
        if (!inventoryRepository.existsByProductVariantId(variant.getId())) {
            throw new ResourceNotFoundException("Inventory not found for SKU: " + sku);
        }
        return new Preflight(user.getId(), variant.getId(), variant.getSku());
    }

    private LocalResult updateLocalInventory(Preflight preflight, int quantity) {
        User user = userRepository.findById(preflight.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user no longer exists"));
        validateAuthorizedUser(user);

        ProductVariant variant = variantRepository.findById(preflight.variantId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product variant no longer exists for SKU: " + preflight.sku()));
        validateVariant(variant, preflight.sku());

        Inventory inventory = inventoryRepository.findByProductVariantIdForUpdate(variant.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for SKU: " + preflight.sku()));
        int previousStock = inventory.getCurrentStock();
        int newStock;
        try {
            newStock = Math.addExact(previousStock, quantity);
        } catch (ArithmeticException exception) {
            throw new BusinessException("Store stock exceeds the supported limit", exception);
        }
        inventory.setCurrentStock(newStock);
        inventoryRepository.save(inventory);

        StockMovement movement = new StockMovement();
        movement.setProductVariant(variant);
        movement.setUser(user);
        movement.setMovementType(StockMovementType.IN);
        movement.setQuantity(quantity);
        movement.setPreviousStock(previousStock);
        movement.setNewStock(newStock);
        movement.setReason(MOVEMENT_REASON);
        movement = movementRepository.save(movement);
        return new LocalResult(newStock, movement.getId());
    }

    private User findAuthorizedUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
        validateAuthorizedUser(user);
        return user;
    }

    private void validateAuthorizedUser(User user) {
        if (Boolean.FALSE.equals(user.getActive())) {
            throw new BusinessException("Authenticated user is inactive");
        }
        if (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.SELLER) {
            throw new BusinessException("Only ADMIN or SELLER can replenish inventory");
        }
    }

    private ProductVariant findUsableVariant(String sku) {
        ProductVariant variant = variantRepository.findBySkuIgnoreCase(sku)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product variant not found with SKU: " + sku));
        validateVariant(variant, sku);
        return variant;
    }

    private void validateVariant(ProductVariant variant, String sku) {
        if (Boolean.FALSE.equals(variant.getActive())) {
            throw new BusinessException("Product variant is inactive: " + sku);
        }
        if (variant.getProduct() == null || Boolean.FALSE.equals(variant.getProduct().getActive())) {
            throw new BusinessException("Product is inactive: " + sku);
        }
    }

    private void compensate(SupplierReplenishmentRequest request, RuntimeException localFailure) {
        try {
            soapClient.restore(request.sku(), request.quantity());
        } catch (RuntimeException compensationFailure) {
            BusinessException exception = new BusinessException(
                    "Store update failed and supplier stock compensation also failed for SKU: "
                            + request.sku(), localFailure);
            exception.addSuppressed(compensationFailure);
            throw exception;
        }
    }

    private SupplierProductResponse toProductResponse(SupplierProductSoapResponse response) {
        return new SupplierProductResponse(
                response.getId(), response.getProductName(), response.getSize(), response.getColor(),
                response.getSku(), response.getPrice(), response.getAvailableStock(), response.getActive());
    }

    private record Preflight(Long userId, Long variantId, String sku) {
    }

    private record LocalResult(int newStock, Long movementId) {
    }
}
