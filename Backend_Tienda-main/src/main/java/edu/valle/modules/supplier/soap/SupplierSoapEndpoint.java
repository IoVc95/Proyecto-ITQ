package edu.valle.modules.supplier.soap;

import edu.valle.modules.supplier.model.SupplierProduct;
import edu.valle.modules.supplier.service.SupplierProductService;
import edu.valle.modules.supplier.soap.dto.GetSupplierProductBySkuRequest;
import edu.valle.modules.supplier.soap.dto.GetSupplierProductBySkuResponse;
import edu.valle.modules.supplier.soap.dto.ReplenishSupplierProductRequest;
import edu.valle.modules.supplier.soap.dto.ReplenishSupplierProductResponse;
import edu.valle.modules.supplier.soap.dto.RestoreSupplierProductStockRequest;
import edu.valle.modules.supplier.soap.dto.RestoreSupplierProductStockResponse;
import edu.valle.modules.supplier.soap.dto.SoapNamespaces;
import edu.valle.modules.supplier.soap.dto.SupplierProductSoapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@RequiredArgsConstructor
public class SupplierSoapEndpoint {

    private final SupplierProductService service;

    @PayloadRoot(namespace = SoapNamespaces.SUPPLIER, localPart = "getSupplierProductBySkuRequest")
    @ResponsePayload
    public GetSupplierProductBySkuResponse getBySku(
            @RequestPayload GetSupplierProductBySkuRequest request) {
        GetSupplierProductBySkuResponse response = new GetSupplierProductBySkuResponse();
        return execute(response, () -> service.findBySku(request.getSku()));
    }

    @PayloadRoot(namespace = SoapNamespaces.SUPPLIER, localPart = "replenishSupplierProductRequest")
    @ResponsePayload
    public ReplenishSupplierProductResponse replenish(
            @RequestPayload ReplenishSupplierProductRequest request) {
        ReplenishSupplierProductResponse response = new ReplenishSupplierProductResponse();
        return execute(response, () -> service.reserve(request.getSku(), request.getQuantity()));
    }

    @PayloadRoot(namespace = SoapNamespaces.SUPPLIER, localPart = "restoreSupplierProductStockRequest")
    @ResponsePayload
    public RestoreSupplierProductStockResponse restore(
            @RequestPayload RestoreSupplierProductStockRequest request) {
        RestoreSupplierProductStockResponse response = new RestoreSupplierProductStockResponse();
        return execute(response, () -> service.restore(request.getSku(), request.getQuantity()));
    }

    private <T extends SupplierProductSoapResponse> T execute(
            T response, SupplierAction action) {
        try {
            populate(response, action.execute());
            response.setSuccess(true);
        } catch (RuntimeException exception) {
            response.setSuccess(false);
            response.setMessage(exception.getMessage());
        }
        return response;
    }

    private void populate(SupplierProductSoapResponse response, SupplierProduct product) {
        response.setId(product.id());
        response.setProductName(product.productName());
        response.setSize(product.size());
        response.setColor(product.color());
        response.setSku(product.sku());
        response.setPrice(product.price());
        response.setAvailableStock(product.availableStock());
        response.setActive(product.active());
    }

    @FunctionalInterface
    private interface SupplierAction {
        SupplierProduct execute();
    }
}
