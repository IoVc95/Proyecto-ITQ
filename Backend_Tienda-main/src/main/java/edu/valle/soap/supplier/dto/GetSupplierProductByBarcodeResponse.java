package edu.valle.soap.supplier.dto;

import java.math.BigDecimal;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "getSupplierProductByBarcodeResponse", namespace = "http://valle.edu/supplier-products")
@XmlType(propOrder = {"success", "message", "id", "name", "barcode", "price", "stock", "active"})
public class GetSupplierProductByBarcodeResponse {

    @XmlElement(name = "success", namespace = "http://valle.edu/supplier-products")
    private boolean success;

    @XmlElement(name = "message", namespace = "http://valle.edu/supplier-products")
    private String message;

    @XmlElement(name = "id", namespace = "http://valle.edu/supplier-products")
    private Long id;

    @XmlElement(name = "name", namespace = "http://valle.edu/supplier-products")
    private String name;

    @XmlElement(name = "barcode", namespace = "http://valle.edu/supplier-products")
    private String barcode;

    @XmlElement(name = "price", namespace = "http://valle.edu/supplier-products")
    private BigDecimal price;

    @XmlElement(name = "stock", namespace = "http://valle.edu/supplier-products")
    private Integer stock;

    @XmlElement(name = "active", namespace = "http://valle.edu/supplier-products")
    private Boolean active;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
