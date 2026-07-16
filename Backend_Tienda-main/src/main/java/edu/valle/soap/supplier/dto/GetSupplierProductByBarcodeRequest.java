package edu.valle.soap.supplier.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "getSupplierProductByBarcodeRequest", namespace = "http://valle.edu/supplier-products")
@XmlType(propOrder = {"barcode"})
public class GetSupplierProductByBarcodeRequest {

    @XmlElement(name = "barcode", namespace = "http://valle.edu/supplier-products", required = true)
    private String barcode;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
