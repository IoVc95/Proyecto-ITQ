package edu.valle.modules.supplier.controller;

import edu.valle.modules.supplier.dto.request.SupplierReplenishmentRequest;
import edu.valle.modules.supplier.dto.response.SupplierProductResponse;
import edu.valle.modules.supplier.dto.response.SupplierReplenishmentResponse;
import edu.valle.modules.supplier.service.SupplierIntegrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/supplier")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SELLER')")
public class SupplierController {

    private final SupplierIntegrationService service;

    @GetMapping("/products/{sku}")
    public SupplierProductResponse getProduct(@PathVariable String sku) {
        return service.getSupplierProduct(sku);
    }

    @PostMapping("/replenishments")
    @ResponseStatus(HttpStatus.CREATED)
    public SupplierReplenishmentResponse replenish(
            Authentication authentication,
            @Valid @RequestBody SupplierReplenishmentRequest request) {
        return service.replenish(authentication.getName(), request);
    }
}
