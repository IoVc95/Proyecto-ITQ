package edu.valle.soap.supplier.dto;

import jakarta.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

    public GetSupplierProductByBarcodeRequest createGetSupplierProductByBarcodeRequest() {
        return new GetSupplierProductByBarcodeRequest();
    }

    public GetSupplierProductByBarcodeResponse createGetSupplierProductByBarcodeResponse() {
        return new GetSupplierProductByBarcodeResponse();
    }

    public ReplenishSupplierProductRequest createReplenishSupplierProductRequest() {
        return new ReplenishSupplierProductRequest();
    }

    public ReplenishSupplierProductResponse createReplenishSupplierProductResponse() {
        return new ReplenishSupplierProductResponse();
    }
}
