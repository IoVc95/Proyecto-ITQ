package edu.valle.modules.supplier.service;

import edu.valle.exception.BusinessException;
import edu.valle.exception.ResourceNotFoundException;
import edu.valle.modules.supplier.model.SupplierProduct;
import edu.valle.modules.supplier.repository.SupplierProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SupplierProductService {

    private final SupplierProductRepository repository;

    @Transactional(transactionManager = "supplierTransactionManager", readOnly = true)
    public SupplierProduct findBySku(String sku) {
        return repository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier product not found with SKU: " + sku));
    }

    @Transactional(transactionManager = "supplierTransactionManager")
    public SupplierProduct reserve(String sku, int quantity) {
        validateQuantity(quantity);
        SupplierProduct current = findBySku(sku);
        if (Boolean.FALSE.equals(current.active())) {
            throw new BusinessException("Supplier product is inactive: " + sku);
        }
        if (!repository.decreaseStock(sku, quantity)) {
            throw new BusinessException("Insufficient supplier stock for SKU: " + sku);
        }
        return findBySku(sku);
    }

    @Transactional(transactionManager = "supplierTransactionManager")
    public SupplierProduct restore(String sku, int quantity) {
        validateQuantity(quantity);
        findBySku(sku);
        if (!repository.restoreStock(sku, quantity)) {
            throw new BusinessException("Could not restore supplier stock for SKU: " + sku);
        }
        return findBySku(sku);
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new BusinessException("Quantity must be greater than zero");
        }
    }
}
