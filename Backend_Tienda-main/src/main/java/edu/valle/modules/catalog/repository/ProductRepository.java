package edu.valle.modules.catalog.repository;
import edu.valle.modules.catalog.entity.Category;
import edu.valle.modules.catalog.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByCategory(Category category);
    List<Product> findByActiveTrue();
    List<Product> findByActiveTrueAndCategoryId(Long categoryId);
}
