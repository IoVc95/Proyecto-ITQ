package edu.valle.soap.supplier.service;

import edu.valle.exception.BadRequestException;
import edu.valle.exception.BusinessException;
import edu.valle.exception.ResourceNotFoundException;
import edu.valle.soap.supplier.model.SupplierProduct;
import edu.valle.soap.supplier.repository.SupplierProductJdbcRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SupplierProductService {

    private final SupplierProductJdbcRepository repository;

    public SupplierProductService(SupplierProductJdbcRepository repository) {
        this.repository = repository;
    }

    public List<SupplierProduct> findAll() {
        return repository.findAll();
    }

    public SupplierProduct findByBarcode(String barcode) {
        return repository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier product", barcode));
    }

    public SupplierProduct decreaseStock(String barcode, int quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        SupplierProduct product = findByBarcode(barcode);

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new BusinessException("Supplier product is inactive");
        }

        if (product.getStock() == null || product.getStock() < quantity) {
            throw new BusinessException("Insufficient supplier product stock");
        }

        repository.decreaseStock(barcode, quantity);
        return findByBarcode(barcode);
    }
}
