package edu.valle.modules.catalog.service.impl;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.valle.common.enums.UserRole;
import edu.valle.exception.BusinessException;
import edu.valle.exception.ResourceNotFoundException;
import edu.valle.modules.catalog.dto.request.ProductRequest;
import edu.valle.modules.catalog.dto.response.ProductResponse;
import edu.valle.modules.catalog.entity.Category;
import edu.valle.modules.catalog.entity.Product;
import edu.valle.modules.catalog.mapper.ProductMapper;
import edu.valle.modules.catalog.repository.CategoryRepository;
import edu.valle.modules.catalog.repository.ProductRepository;
import edu.valle.modules.inventory.repository.InventoryRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductMapper productMapper;

    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(
                productRepository,
                categoryRepository,
                inventoryRepository,
                productMapper
        );
    }

    @Test
    void findsProductByExistingBarcode() {
        Product product = product(1L, "7501234567890");
        ProductResponse response = response();
        when(productRepository.findByBarcode("7501234567890")).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(response);

        assertSame(response, productService.findByBarcode("7501234567890"));
    }

    @Test
    void legacyCodeSearchUsesBarcodeOnly() {
        Product product = product(1L, "7501234567890");
        ProductResponse response = response();
        when(productRepository.findByBarcode("7501234567890")).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(response);

        assertSame(response, productService.findByCode("7501234567890"));
        verify(productRepository).findByBarcode("7501234567890");
    }

    @Test
    void missingBarcodeReturnsNotFound() {
        when(productRepository.findByBarcode("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.findByBarcode("missing"));
    }

    @Test
    void duplicateBarcodeIsRejectedOnCreate() {
        ProductRequest request = request("7501234567890");
        when(productRepository.findBySku(request.sku())).thenReturn(Optional.empty());
        when(productRepository.findByBarcode(request.barcode()))
                .thenReturn(Optional.of(product(2L, request.barcode())));

        assertThrows(BusinessException.class, () -> productService.create(request));
        verify(productRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private ProductRequest request(String barcode) {
        return new ProductRequest(
                "Product",
                "Description",
                "SKU-1",
                barcode,
                new BigDecimal("10.00"),
                new BigDecimal("5.00"),
                true,
                1L
        );
    }

    private Product product(Long id, String barcode) {
        Product product = new Product();
        product.setId(id);
        product.setBarcode(barcode);
        return product;
    }

    private ProductResponse response() {
        return new ProductResponse(
                1L,
                "Product",
                "Description",
                "SKU-1",
                "7501234567890",
                new BigDecimal("10.00"),
                new BigDecimal("5.00"),
                true,
                1L,
                "Category",
                null,
                null
        );
    }
}
