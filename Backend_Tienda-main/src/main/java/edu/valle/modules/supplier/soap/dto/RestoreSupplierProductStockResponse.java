package edu.valle.modules.supplier.soap.dto;
import jakarta.xml.bind.annotation.*;
@XmlRootElement(name = "restoreSupplierProductStockResponse", namespace = SoapNamespaces.SUPPLIER)
@XmlAccessorType(XmlAccessType.FIELD)
public class RestoreSupplierProductStockResponse extends SupplierProductSoapResponse { }
