package edu.valle.modules.supplier.soap.dto;
import jakarta.xml.bind.annotation.*;
@XmlRootElement(name = "getSupplierProductBySkuResponse", namespace = SoapNamespaces.SUPPLIER)
@XmlAccessorType(XmlAccessType.FIELD)
public class GetSupplierProductBySkuResponse extends SupplierProductSoapResponse { }
