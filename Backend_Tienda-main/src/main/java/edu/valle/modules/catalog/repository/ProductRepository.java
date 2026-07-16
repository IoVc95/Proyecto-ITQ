package edu.valle.modules.catalog.repository;

import edu.valle.modules.catalog.entity.Category;
import edu.valle.modules.catalog.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    Optional<Product> findByBarcode(String barcode);

    List<Product> findByCategory(Category category);

    List<Product> findByActiveTrue();

    boolean existsBySku(String sku);
}
