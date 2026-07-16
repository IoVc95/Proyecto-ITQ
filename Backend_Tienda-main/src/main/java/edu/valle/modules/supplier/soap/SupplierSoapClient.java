package edu.valle.modules.supplier.soap;

import edu.valle.exception.BusinessException;
import edu.valle.modules.supplier.soap.dto.GetSupplierProductBySkuRequest;
import edu.valle.modules.supplier.soap.dto.GetSupplierProductBySkuResponse;
import edu.valle.modules.supplier.soap.dto.ReplenishSupplierProductRequest;
import edu.valle.modules.supplier.soap.dto.ReplenishSupplierProductResponse;
import edu.valle.modules.supplier.soap.dto.RestoreSupplierProductStockRequest;
import edu.valle.modules.supplier.soap.dto.RestoreSupplierProductStockResponse;
import edu.valle.modules.supplier.soap.dto.SupplierProductSoapResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

@Component
@RequiredArgsConstructor
public class SupplierSoapClient {

    private final WebServiceTemplate supplierWebServiceTemplate;

    public GetSupplierProductBySkuResponse getBySku(String sku) {
        GetSupplierProductBySkuRequest request = new GetSupplierProductBySkuRequest();
        request.setSku(sku);
        return validate((GetSupplierProductBySkuResponse)
                supplierWebServiceTemplate.marshalSendAndReceive(request));
    }

    public ReplenishSupplierProductResponse reserve(String sku, int quantity) {
        ReplenishSupplierProductRequest request = new ReplenishSupplierProductRequest();
        request.setSku(sku);
        request.setQuantity(quantity);
        return validate((ReplenishSupplierProductResponse)
                supplierWebServiceTemplate.marshalSendAndReceive(request));
    }

    public RestoreSupplierProductStockResponse restore(String sku, int quantity) {
        RestoreSupplierProductStockRequest request = new RestoreSupplierProductStockRequest();
        request.setSku(sku);
        request.setQuantity(quantity);
        return validate((RestoreSupplierProductStockResponse)
                supplierWebServiceTemplate.marshalSendAndReceive(request));
    }

    private <T extends SupplierProductSoapResponse> T validate(T response) {
        if (response == null) {
            throw new BusinessException("Supplier SOAP returned an empty response");
        }
        if (!response.isSuccess()) {
            throw new BusinessException(response.getMessage());
        }
        return response;
    }
}
