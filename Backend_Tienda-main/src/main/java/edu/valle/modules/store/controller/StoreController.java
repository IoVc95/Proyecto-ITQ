package edu.valle.modules.store.controller;
import edu.valle.modules.store.dto.response.*; import edu.valle.modules.store.service.StoreService; import java.util.List; import lombok.RequiredArgsConstructor; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/store") @RequiredArgsConstructor public class StoreController {private final StoreService service;
 @GetMapping("/categories") public List<StoreCategoryResponse> categories(){return service.categories();}
 @GetMapping("/products") public List<StoreProductResponse> products(@RequestParam(required=false) Long categoryId){return service.products(categoryId);}
 @GetMapping("/products/{id}") public StoreProductResponse product(@PathVariable Long id){return service.product(id);}}
