package edu.valle.modules.supplier.soap.dto;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "getSupplierProductBySkuRequest", namespace = SoapNamespaces.SUPPLIER)
@XmlAccessorType(XmlAccessType.FIELD)
public class GetSupplierProductBySkuRequest {
    @XmlElement(required = true)
    private String sku;
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
}
