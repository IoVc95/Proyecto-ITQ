package edu.valle.soap.client;

import edu.valle.soap.supplier.dto.GetSupplierProductByBarcodeRequest;
import edu.valle.soap.supplier.dto.GetSupplierProductByBarcodeResponse;
import edu.valle.soap.supplier.dto.ReplenishSupplierProductRequest;
import edu.valle.soap.supplier.dto.ReplenishSupplierProductResponse;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

@Component
public class SupplierSoapClient {

    private final WebServiceTemplate webServiceTemplate;

    public SupplierSoapClient(WebServiceTemplate supplierWebServiceTemplate) {
        this.webServiceTemplate = supplierWebServiceTemplate;
    }

    public GetSupplierProductByBarcodeResponse getSupplierProductByBarcode(String barcode) {
        GetSupplierProductByBarcodeRequest request = new GetSupplierProductByBarcodeRequest();
        request.setBarcode(barcode);

        return (GetSupplierProductByBarcodeResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public ReplenishSupplierProductResponse replenishSupplierProduct(String barcode, int quantity) {
        ReplenishSupplierProductRequest request = new ReplenishSupplierProductRequest();
        request.setBarcode(barcode);
        request.setQuantity(quantity);

        return (ReplenishSupplierProductResponse) webServiceTemplate.marshalSendAndReceive(request);
    }
}
