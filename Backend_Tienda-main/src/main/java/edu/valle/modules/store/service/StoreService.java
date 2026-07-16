package edu.valle.modules.store.service;
import edu.valle.modules.store.dto.response.*; import java.util.List;
public interface StoreService {List<StoreCategoryResponse> categories();List<StoreProductResponse> products(Long categoryId);StoreProductResponse product(Long id);}
