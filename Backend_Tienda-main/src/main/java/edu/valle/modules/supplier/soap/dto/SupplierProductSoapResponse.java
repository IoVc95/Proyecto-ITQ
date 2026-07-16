package edu.valle.modules.supplier.soap.dto;

import jakarta.xml.bind.annotation.*;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "supplierProductResponse", namespace = SoapNamespaces.SUPPLIER)
public class SupplierProductSoapResponse {
    private boolean success;
    private String message;
    private Long id;
    private String productName;
    private String size;
    private String color;
    private String sku;
    private BigDecimal price;
    private Integer availableStock;
    private Boolean active;
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getAvailableStock() { return availableStock; }
    public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
