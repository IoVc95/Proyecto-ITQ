package edu.valle.modules.supplier.soap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import edu.valle.exception.ResourceNotFoundException;
import edu.valle.modules.supplier.model.SupplierProduct;
import edu.valle.modules.supplier.service.SupplierProductService;
import edu.valle.modules.supplier.soap.dto.GetSupplierProductBySkuRequest;
import edu.valle.modules.supplier.soap.dto.GetSupplierProductBySkuResponse;
import edu.valle.modules.supplier.soap.dto.ReplenishSupplierProductRequest;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupplierSoapEndpointTest {

    @Mock
    private SupplierProductService service;
    private SupplierSoapEndpoint endpoint;

    @BeforeEach
    void setUp() {
        endpoint = new SupplierSoapEndpoint(service);
    }

    @Test
    void returnsProductForExistingSku() {
        when(service.findBySku("SKU-1")).thenReturn(product(7));
        GetSupplierProductBySkuRequest request = new GetSupplierProductBySkuRequest();
        request.setSku("SKU-1");

        GetSupplierProductBySkuResponse response = endpoint.getBySku(request);

        assertTrue(response.isSuccess());
        assertEquals("SKU-1", response.getSku());
        assertEquals(7, response.getAvailableStock());
    }

    @Test
    void representsUnknownSkuAsFailedSoapResponse() {
        when(service.findBySku("UNKNOWN"))
                .thenThrow(new ResourceNotFoundException("Supplier product not found"));
        GetSupplierProductBySkuRequest request = new GetSupplierProductBySkuRequest();
        request.setSku("UNKNOWN");

        GetSupplierProductBySkuResponse response = endpoint.getBySku(request);

        assertFalse(response.isSuccess());
        assertEquals("Supplier product not found", response.getMessage());
    }

    @Test
    void replenishmentReturnsRemainingSupplierStock() {
        when(service.reserve("SKU-1", 3)).thenReturn(product(4));
        ReplenishSupplierProductRequest request = new ReplenishSupplierProductRequest();
        request.setSku("SKU-1");
        request.setQuantity(3);

        assertEquals(4, endpoint.replenish(request).getAvailableStock());
    }

    private SupplierProduct product(int stock) {
        return new SupplierProduct(1L, "Camiseta", "M", "Negro", "SKU-1",
                new BigDecimal("8.50"), stock, true);
    }
}
