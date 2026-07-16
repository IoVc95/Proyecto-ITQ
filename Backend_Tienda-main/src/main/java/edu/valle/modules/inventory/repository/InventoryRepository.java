package edu.valle.modules.inventory.repository;
import edu.valle.modules.catalog.entity.ProductVariant; import edu.valle.modules.inventory.entity.Inventory; import edu.valle.modules.reports.dto.response.LowStockReportResponse; import java.util.*; import org.springframework.data.jpa.repository.*;
public interface InventoryRepository extends JpaRepository<Inventory,Long> {
 Optional<Inventory> findByProductVariant(ProductVariant v); Optional<Inventory> findByProductVariantId(Long id); boolean existsByProductVariantId(Long id);
 @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE) @Query("select i from Inventory i where i.productVariant.id=:variantId") Optional<Inventory> findByProductVariantIdForUpdate(Long variantId);
 @Query("select i from Inventory i where i.currentStock <= i.minStock") List<Inventory> findLowStock();
 @Query("select new edu.valle.modules.reports.dto.response.LowStockReportResponse(p.id,p.name,v.sku,i.currentStock,i.minStock) from Inventory i join i.productVariant v join v.product p where i.currentStock <= i.minStock order by i.currentStock asc,p.name asc") List<LowStockReportResponse> findLowStockReport();
}
