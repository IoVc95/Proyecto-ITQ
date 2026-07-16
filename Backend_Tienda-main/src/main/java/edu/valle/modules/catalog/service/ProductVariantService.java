package edu.valle.modules.catalog.service;
import edu.valle.modules.catalog.dto.request.ProductVariantRequest;
import edu.valle.modules.catalog.dto.response.ProductVariantResponse;
import java.util.List;
public interface ProductVariantService {
 ProductVariantResponse create(ProductVariantRequest r); List<ProductVariantResponse> findAll();
 ProductVariantResponse findById(Long id); List<ProductVariantResponse> findByProductId(Long id);
 ProductVariantResponse update(Long id,ProductVariantRequest r); void deactivate(Long id);
}
