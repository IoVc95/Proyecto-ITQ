package edu.valle.modules.sales.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CreateSaleItemRequestTest {

    @Test
    void saleItemsContinueUsingProductId() {
        CreateSaleItemRequest request = new CreateSaleItemRequest(15L, 2);

        assertEquals(15L, request.productId());
        assertEquals(2, request.quantity());
    }
}
