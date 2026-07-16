package edu.valle.modules.inventory.repository;

import edu.valle.modules.catalog.entity.Product;
import edu.valle.modules.inventory.entity.Inventory;
import edu.valle.modules.reports.dto.response.LowStockReportResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProduct(Product product);

    Optional<Inventory> findByProductId(Long productId);

    @Query("select i from Inventory i where i.currentStock <= i.minStock")
    List<Inventory> findLowStock();

    @Query("""
            select new edu.valle.modules.reports.dto.response.LowStockReportResponse(
                p.id,
                p.name,
                p.sku,
                i.currentStock,
                i.minStock
            )
            from Inventory i
            join i.product p
            where i.currentStock <= i.minStock
            order by i.currentStock asc, p.name asc
            """)
    List<LowStockReportResponse> findLowStockReport();
}
