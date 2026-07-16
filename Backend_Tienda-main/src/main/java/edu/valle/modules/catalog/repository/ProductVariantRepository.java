package edu.valle.modules.catalog.repository;
import edu.valle.modules.catalog.entity.ProductVariant;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductVariantRepository extends JpaRepository<ProductVariant,Long> {
 Optional<ProductVariant> findBySkuIgnoreCase(String sku);
 boolean existsByProductIdAndSizeIgnoreCaseAndColorIgnoreCase(Long productId,String size,String color);
 List<ProductVariant> findByProductId(Long productId);
 List<ProductVariant> findByProductIdAndActiveTrue(Long productId);
}
