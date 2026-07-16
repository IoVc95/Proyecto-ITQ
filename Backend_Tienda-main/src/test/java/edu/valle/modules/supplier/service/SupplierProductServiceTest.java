package edu.valle.modules.supplier.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.valle.exception.BusinessException;
import edu.valle.exception.ResourceNotFoundException;
import edu.valle.modules.supplier.model.SupplierProduct;
import edu.valle.modules.supplier.repository.SupplierProductRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupplierProductServiceTest {

    @Mock
    private SupplierProductRepository repository;
    private SupplierProductService service;

    @BeforeEach
    void setUp() {
        service = new SupplierProductService(repository);
    }

    @Test
    void findsSupplierProductBySku() {
        SupplierProduct product = product(true, 10);
        when(repository.findBySku("SKU-1")).thenReturn(Optional.of(product));

        assertEquals(product, service.findBySku("SKU-1"));
    }

    @Test
    void rejectsUnknownSku() {
        when(repository.findBySku("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findBySku("UNKNOWN"));
    }

    @Test
    void reservesWithAtomicConditionalUpdate() {
        when(repository.findBySku("SKU-1"))
                .thenReturn(Optional.of(product(true, 10)), Optional.of(product(true, 6)));
        when(repository.decreaseStock("SKU-1", 4)).thenReturn(true);

        SupplierProduct updated = service.reserve("SKU-1", 4);

        assertEquals(6, updated.availableStock());
        verify(repository).decreaseStock("SKU-1", 4);
    }

    @Test
    void rejectsInsufficientOrConcurrentStock() {
        when(repository.findBySku("SKU-1")).thenReturn(Optional.of(product(true, 2)));
        when(repository.decreaseStock("SKU-1", 4)).thenReturn(false);

        assertThrows(BusinessException.class, () -> service.reserve("SKU-1", 4));
    }

    @Test
    void rejectsInactiveProductAndInvalidQuantity() {
        when(repository.findBySku("SKU-1")).thenReturn(Optional.of(product(false, 10)));

        assertThrows(BusinessException.class, () -> service.reserve("SKU-1", 1));
        assertThrows(BusinessException.class, () -> service.reserve("SKU-1", 0));
    }

    private SupplierProduct product(boolean active, int stock) {
        return new SupplierProduct(1L, "Camiseta", "M", "Negro", "SKU-1",
                new BigDecimal("8.50"), stock, active);
    }
}
