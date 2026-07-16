package edu.valle.soap.supplier.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "replenishSupplierProductRequest", namespace = "http://valle.edu/supplier-products")
@XmlType(propOrder = {"barcode", "quantity"})
public class ReplenishSupplierProductRequest {

    @XmlElement(name = "barcode", namespace = "http://valle.edu/supplier-products", required = true)
    private String barcode;

    @XmlElement(name = "quantity", namespace = "http://valle.edu/supplier-products")
    private int quantity;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
