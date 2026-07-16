package edu.valle.modules.catalog.service.impl;

import edu.valle.exception.BusinessException;
import edu.valle.exception.ResourceNotFoundException;
import edu.valle.modules.catalog.dto.request.ProductRequest;
import edu.valle.modules.catalog.dto.response.ProductResponse;
import edu.valle.modules.catalog.entity.Category;
import edu.valle.modules.catalog.entity.Product;
import edu.valle.modules.catalog.mapper.ProductMapper;
import edu.valle.modules.catalog.repository.CategoryRepository;
import edu.valle.modules.catalog.repository.ProductRepository;
import edu.valle.modules.catalog.service.ProductService;
import edu.valle.modules.inventory.entity.Inventory;
import edu.valle.modules.inventory.repository.InventoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        validateUniqueProductCodes(request, null);
        Category category = findCategory(request.categoryId());
        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        if (product.getActive() == null) {
            product.setActive(true);
        }
        Product savedProduct = productRepository.save(product);
        Inventory inventory = new Inventory();
        inventory.setProduct(savedProduct);
        inventory.setCurrentStock(0);
        inventory.setMinStock(0);
        inventoryRepository.save(inventory);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return productMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findByBarcode(String barcode) {
        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with barcode: " + barcode));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findByCode(String code) {
        return findByBarcode(code);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findEntityById(id);
        validateUniqueProductCodes(request, id);
        Category category = findCategory(request.categoryId());
        productMapper.updateEntity(request, product);
        product.setCategory(category);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public void deactivate(Long id) {
        Product product = findEntityById(id);
        product.setActive(false);
        productRepository.save(product);
    }

    private Product findEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    private Category findCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));
        if (Boolean.FALSE.equals(category.getActive())) {
            throw new BusinessException("Category is inactive");
        }
        return category;
    }

    private void validateUniqueProductCodes(ProductRequest request, Long currentProductId) {
        productRepository.findBySku(request.sku())
                .filter(existing -> !existing.getId().equals(currentProductId))
                .ifPresent(existing -> {
                    throw new BusinessException("Product SKU already exists");
                });
        if (request.barcode() != null && !request.barcode().isBlank()) {
            productRepository.findByBarcode(request.barcode())
                    .filter(existing -> !existing.getId().equals(currentProductId))
                    .ifPresent(existing -> {
                        throw new BusinessException("Product barcode already exists");
                    });
        }
    }
}
