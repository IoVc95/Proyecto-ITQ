package edu.valle.soap.supplier.endpoint;

import edu.valle.soap.supplier.dto.GetSupplierProductByBarcodeRequest;
import edu.valle.soap.supplier.dto.GetSupplierProductByBarcodeResponse;
import edu.valle.soap.supplier.dto.ReplenishSupplierProductRequest;
import edu.valle.soap.supplier.dto.ReplenishSupplierProductResponse;
import edu.valle.soap.supplier.model.SupplierProduct;
import edu.valle.soap.supplier.service.SupplierProductService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class SupplierProductEndpoint {

    private static final String NAMESPACE_URI = "http://valle.edu/supplier-products";

    private final SupplierProductService supplierProductService;

    public SupplierProductEndpoint(SupplierProductService supplierProductService) {
        this.supplierProductService = supplierProductService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getSupplierProductByBarcodeRequest")
    @ResponsePayload
    public GetSupplierProductByBarcodeResponse getSupplierProductByBarcode(
            @RequestPayload GetSupplierProductByBarcodeRequest request) {

        GetSupplierProductByBarcodeResponse response = new GetSupplierProductByBarcodeResponse();

        try {
            SupplierProduct product = supplierProductService.findByBarcode(request.getBarcode());
            response.setSuccess(true);
            response.setMessage("Supplier product found");
            fillResponse(response, product);
        } catch (Exception exception) {
            response.setSuccess(false);
            response.setMessage(exception.getMessage());
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "replenishSupplierProductRequest")
    @ResponsePayload
    public ReplenishSupplierProductResponse replenishSupplierProduct(
            @RequestPayload ReplenishSupplierProductRequest request) {

        ReplenishSupplierProductResponse response = new ReplenishSupplierProductResponse();

        try {
            SupplierProduct product = supplierProductService.decreaseStock(
                    request.getBarcode(),
                    request.getQuantity());
            response.setSuccess(true);
            response.setMessage("Supplier product stock updated");
            fillResponse(response, product);
        } catch (Exception exception) {
            response.setSuccess(false);
            response.setMessage(exception.getMessage());
        }

        return response;
    }

    private void fillResponse(GetSupplierProductByBarcodeResponse response, SupplierProduct product) {
        response.setId(product.getId());
        response.setName(product.getName());
        response.setBarcode(product.getBarcode());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setActive(product.getActive());
    }

    private void fillResponse(ReplenishSupplierProductResponse response, SupplierProduct product) {
        response.setId(product.getId());
        response.setName(product.getName());
        response.setBarcode(product.getBarcode());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setActive(product.getActive());
    }
}
