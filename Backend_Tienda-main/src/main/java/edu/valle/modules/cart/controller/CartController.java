package edu.valle.modules.cart.controller;
import edu.valle.modules.cart.dto.request.CartItemRequest; import edu.valle.modules.cart.dto.response.CartResponse; import edu.valle.modules.cart.service.CartService; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/cart") @RequiredArgsConstructor @PreAuthorize("hasRole('CUSTOMER')") public class CartController {private final CartService service;
 @GetMapping public CartResponse get(Authentication a){return service.get(a.getName());}
 @PostMapping("/items") public CartResponse add(Authentication a,@Valid @RequestBody CartItemRequest r){return service.add(a.getName(),r);}
 @PutMapping("/items/{itemId}") public CartResponse update(Authentication a,@PathVariable Long itemId,@Valid @RequestBody CartItemRequest r){return service.update(a.getName(),itemId,r);}
 @DeleteMapping("/items/{itemId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void remove(Authentication a,@PathVariable Long itemId){service.remove(a.getName(),itemId);}
 @DeleteMapping @ResponseStatus(HttpStatus.NO_CONTENT) public void clear(Authentication a){service.clear(a.getName());}}
