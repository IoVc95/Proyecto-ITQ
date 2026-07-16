package edu.valle.modules.payments.repository;

import edu.valle.common.enums.PaymentMethod;
import edu.valle.modules.payments.entity.Payment;
import edu.valle.modules.reports.dto.response.PaymentMethodSummaryResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByOrderByPaidAtDesc();

    List<Payment> findBySaleIdOrderByPaidAtAsc(Long saleId);

    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);

    List<Payment> findByPaidAtBetweenOrderByPaidAtAsc(LocalDateTime from, LocalDateTime to);

    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.sale.id = :saleId")
    BigDecimal sumAmountBySaleId(Long saleId);

    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.paidAt between :from and :to and p.sale.status <> edu.valle.common.enums.SaleStatus.CANCELLED")
    BigDecimal sumAmountByPaidAtBetween(LocalDateTime from, LocalDateTime to);

    @Query(value = """
            select cast(paid_at as date) as income_date, coalesce(sum(amount), 0) as income
            from payments p join sales s on s.id=p.sale_id
            where p.paid_at between :from and :to and s.status <> 'CANCELLED'
            group by cast(paid_at as date)
            order by income_date
            """, nativeQuery = true)
    List<Object[]> findDailyIncome(LocalDateTime from, LocalDateTime to);

    @Query("""
            select new edu.valle.modules.reports.dto.response.PaymentMethodSummaryResponse(
                p.paymentMethod,
                count(p),
                coalesce(sum(p.amount), 0)
            )
            from Payment p
            where p.paidAt between :from and :to
              and p.sale.status <> edu.valle.common.enums.SaleStatus.CANCELLED
            group by p.paymentMethod
            order by sum(p.amount) desc
            """)
    List<PaymentMethodSummaryResponse> summarizeByPaymentMethod(LocalDateTime from, LocalDateTime to);
}
