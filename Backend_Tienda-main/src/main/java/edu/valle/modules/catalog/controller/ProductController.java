package edu.valle.modules.catalog.controller;
import edu.valle.modules.catalog.dto.request.ProductRequest;
import edu.valle.modules.catalog.dto.response.ProductResponse;
import edu.valle.modules.catalog.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/products") @RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SELLER')")
public class ProductController {
    private final ProductService service;
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ProductResponse create(@Valid @RequestBody ProductRequest r){return service.create(r);}
    @GetMapping public List<ProductResponse> findAll(){return service.findAll();}
    @GetMapping("/{id}") public ProductResponse findById(@PathVariable Long id){return service.findById(id);}
    @PutMapping("/{id}") public ProductResponse update(@PathVariable Long id,@Valid @RequestBody ProductRequest r){return service.update(id,r);}
    @PatchMapping("/{id}/deactivate") @ResponseStatus(HttpStatus.NO_CONTENT) public void deactivate(@PathVariable Long id){service.deactivate(id);}
}
