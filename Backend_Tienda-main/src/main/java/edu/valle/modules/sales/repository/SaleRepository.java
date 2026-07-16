package edu.valle.modules.sales.repository;

import edu.valle.common.enums.SaleStatus;
import edu.valle.modules.reports.dto.response.UserSalesResponse;
import edu.valle.modules.sales.entity.Sale;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    Optional<Sale> findBySaleNumber(String saleNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Sale s where s.id = :id")
    Optional<Sale> findByIdForUpdate(Long id);

    List<Sale> findByStatus(SaleStatus status);

    List<Sale> findBySaleDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Sale> findByUserId(Long userId);

    long countBySaleDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    long countBySaleDateBetweenAndStatusNot(
            LocalDateTime startDate,
            LocalDateTime endDate,
            SaleStatus excludedStatus
    );

    @Query("select coalesce(sum(s.total), 0) from Sale s where s.saleDate between :startDate and :endDate")
    java.math.BigDecimal sumTotalBySaleDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = """
            select date_trunc('month', sale_date) as month_start,
                   count(*) as total_sales,
                   coalesce(sum(total), 0) as sales_amount
            from sales
            where sale_date between :from and :to
              and status <> 'CANCELLED'
            group by date_trunc('month', sale_date)
            order by month_start
            """, nativeQuery = true)
    List<Object[]> findMonthlySales(LocalDateTime from, LocalDateTime to);

    @Query("""
            select new edu.valle.modules.reports.dto.response.UserSalesResponse(
                u.id,
                u.username,
                u.fullName,
                count(s),
                coalesce(sum(s.total), 0)
            )
            from Sale s
            join s.user u
            where s.saleDate between :from and :to
              and s.status <> :excludedStatus
            group by u.id, u.username, u.fullName
            order by sum(s.total) desc
            """)
    List<UserSalesResponse> summarizeSalesByUser(
            LocalDateTime from,
            LocalDateTime to,
            SaleStatus excludedStatus
    );
}
