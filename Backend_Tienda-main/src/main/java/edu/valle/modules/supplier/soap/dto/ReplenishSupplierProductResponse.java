package edu.valle.modules.supplier.soap.dto;
import jakarta.xml.bind.annotation.*;
@XmlRootElement(name = "replenishSupplierProductResponse", namespace = SoapNamespaces.SUPPLIER)
@XmlAccessorType(XmlAccessType.FIELD)
public class ReplenishSupplierProductResponse extends SupplierProductSoapResponse { }
