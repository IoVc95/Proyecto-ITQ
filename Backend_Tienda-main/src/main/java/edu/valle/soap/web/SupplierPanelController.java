package edu.valle.soap.web;

import edu.valle.soap.client.SupplierSoapClient;
import edu.valle.soap.supplier.dto.GetSupplierProductByBarcodeResponse;
import edu.valle.soap.supplier.dto.ReplenishSupplierProductResponse;
import edu.valle.modules.inventory.dto.response.StockMovementResponse;
import edu.valle.modules.inventory.service.InventoryService;
import edu.valle.soap.supplier.service.SupplierProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SupplierPanelController {

    private final SupplierProductService supplierProductService;
    private final SupplierSoapClient supplierSoapClient;
    private final InventoryService inventoryService;

    public SupplierPanelController(
            SupplierProductService supplierProductService,
            SupplierSoapClient supplierSoapClient,
            InventoryService inventoryService) {
        this.supplierProductService = supplierProductService;
        this.supplierSoapClient = supplierSoapClient;
        this.inventoryService = inventoryService;
    }

    @GetMapping("/supplier-panel/products")
    public String products(Model model) {
        model.addAttribute("products", supplierProductService.findAll());
        return "supplier/products";
    }

    @GetMapping("/supplier-panel/search")
    public String search() {
        return "supplier/search";
    }

    @PostMapping("/supplier-panel/search")
    public String searchByBarcode(@RequestParam String barcode, Model model) {
        GetSupplierProductByBarcodeResponse response =
                supplierSoapClient.getSupplierProductByBarcode(barcode);
        model.addAttribute("barcode", barcode);
        model.addAttribute("result", response);
        return "supplier/search";
    }

    @GetMapping("/supplier-panel/replenish")
    public String replenish() {
        return "supplier/replenish";
    }

    @PostMapping("/supplier-panel/replenish")
    public String replenishProduct(
            @RequestParam String barcode,
            @RequestParam int quantity,
            Model model) {

        ReplenishSupplierProductResponse response =
                supplierSoapClient.replenishSupplierProduct(barcode, quantity);
        model.addAttribute("barcode", barcode);
        model.addAttribute("quantity", quantity);
        model.addAttribute("result", response);

        if (response.isSuccess()) {
            try {
                StockMovementResponse movement =
                        inventoryService.increaseStockFromSupplier(barcode, quantity);
                model.addAttribute("storeUpdated", true);
                model.addAttribute("storeMessage", "Stock de tienda actualizado correctamente");
                model.addAttribute("storeProductName", movement.productName());
                model.addAttribute("storeStock", movement.newStock());
            } catch (Exception exception) {
                model.addAttribute("storeUpdated", false);
                model.addAttribute("storeMessage", exception.getMessage());
            }
        }

        return "supplier/replenish";
    }
}
