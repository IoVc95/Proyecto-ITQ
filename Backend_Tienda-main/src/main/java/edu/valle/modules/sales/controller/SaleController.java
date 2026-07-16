package edu.valle.modules.sales.controller;
import edu.valle.modules.sales.dto.request.*; import edu.valle.modules.sales.dto.response.SaleResponse; import edu.valle.modules.sales.service.SaleService; import jakarta.validation.Valid; import java.util.List; import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/sales") @RequiredArgsConstructor public class SaleController {private final SaleService service;
 @PostMapping @PreAuthorize("hasAnyRole('ADMIN','SELLER')") @ResponseStatus(HttpStatus.CREATED) public SaleResponse create(@Valid @RequestBody CreateSaleRequest r){return service.create(r);}
 @PostMapping("/checkout") @PreAuthorize("hasRole('CUSTOMER')") @ResponseStatus(HttpStatus.CREATED) public SaleResponse checkout(Authentication a,@Valid @RequestBody CheckoutRequest r){return service.checkout(a.getName(),r);}
 @GetMapping("/my-orders") @PreAuthorize("hasRole('CUSTOMER')") public List<SaleResponse> myOrders(Authentication a){return service.findMyOrders(a.getName());}
 @GetMapping("/my-orders/{id}") @PreAuthorize("hasRole('CUSTOMER')") public SaleResponse myOrder(Authentication a,@PathVariable Long id){return service.findMyOrder(a.getName(),id);}
 @GetMapping @PreAuthorize("hasAnyRole('ADMIN','SELLER')") public List<SaleResponse> all(){return service.findAll();}
 @GetMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','SELLER')") public SaleResponse find(@PathVariable Long id){return service.findById(id);}
 @PatchMapping("/{id}/status") @PreAuthorize("hasAnyRole('ADMIN','SELLER')") public SaleResponse status(@PathVariable Long id,@Valid @RequestBody UpdateSaleStatusRequest r){return service.updateStatus(id,r);}}
