package edu.valle.modules.supplier.soap.dto;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "restoreSupplierProductStockRequest", namespace = SoapNamespaces.SUPPLIER)
@XmlAccessorType(XmlAccessType.FIELD)
public class RestoreSupplierProductStockRequest {
    @XmlElement(required = true) private String sku;
    private int quantity;
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
