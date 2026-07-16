package edu.valle.modules.sales.repository;

import edu.valle.modules.sales.entity.SaleItem;
import edu.valle.modules.reports.dto.response.TopProductResponse;
import edu.valle.modules.reports.dto.response.BestSellingProductResponse;
import edu.valle.modules.reports.dto.response.CategorySalesResponse;
import edu.valle.common.enums.SaleStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    List<SaleItem> findBySaleId(Long saleId);

    List<SaleItem> findByProductId(Long productId);

    @Query("""
            select new edu.valle.modules.reports.dto.response.TopProductResponse(
                p.id,
                p.name,
                sum(i.quantity),
                sum(i.subtotal)
            )
            from SaleItem i
            join i.product p
            join i.sale s
            where s.saleDate between :startDate and :endDate
              and s.status <> :excludedStatus
            group by p.id, p.name
            order by sum(i.quantity) desc
            """)
    List<TopProductResponse> findTopProducts(
            LocalDateTime startDate,
            LocalDateTime endDate,
            SaleStatus excludedStatus
    );

    @Query("""
            select new edu.valle.modules.reports.dto.response.BestSellingProductResponse(
                p.id,
                p.name,
                sum(i.quantity),
                coalesce(sum(i.subtotal), 0)
            )
            from SaleItem i
            join i.product p
            join i.sale s
            where s.saleDate between :from and :to
              and s.status <> :excludedStatus
            group by p.id, p.name
            order by sum(i.quantity) desc, sum(i.subtotal) desc
            """)
    List<BestSellingProductResponse> findBestSellingProducts(
            LocalDateTime from,
            LocalDateTime to,
            SaleStatus excludedStatus
    );

    @Query("""
            select new edu.valle.modules.reports.dto.response.CategorySalesResponse(
                c.id,
                c.name,
                sum(i.quantity),
                coalesce(sum(i.subtotal), 0)
            )
            from SaleItem i
            join i.product p
            join p.category c
            join i.sale s
            where s.saleDate between :from and :to
              and s.status <> :excludedStatus
            group by c.id, c.name
            order by sum(i.subtotal) desc
            """)
    List<CategorySalesResponse> summarizeSalesByCategory(
            LocalDateTime from,
            LocalDateTime to,
            SaleStatus excludedStatus
    );
}
