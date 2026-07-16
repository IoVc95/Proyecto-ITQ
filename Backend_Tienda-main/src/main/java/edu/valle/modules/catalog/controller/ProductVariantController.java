package edu.valle.modules.catalog.controller;
import edu.valle.modules.catalog.dto.request.ProductVariantRequest;
import edu.valle.modules.catalog.dto.response.ProductVariantResponse;
import edu.valle.modules.catalog.service.ProductVariantService;
import jakarta.validation.Valid; import java.util.List; import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/product-variants") @RequiredArgsConstructor @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
public class ProductVariantController {private final ProductVariantService service;
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public ProductVariantResponse create(@Valid @RequestBody ProductVariantRequest r){return service.create(r);}
 @GetMapping public List<ProductVariantResponse> all(){return service.findAll();}
 @GetMapping("/{id}") public ProductVariantResponse one(@PathVariable Long id){return service.findById(id);}
 @GetMapping("/by-product/{productId}") public List<ProductVariantResponse> byProduct(@PathVariable Long productId){return service.findByProductId(productId);}
 @PutMapping("/{id}") public ProductVariantResponse update(@PathVariable Long id,@Valid @RequestBody ProductVariantRequest r){return service.update(id,r);}
 @PatchMapping("/{id}/deactivate") @ResponseStatus(HttpStatus.NO_CONTENT) public void deactivate(@PathVariable Long id){service.deactivate(id);}}
