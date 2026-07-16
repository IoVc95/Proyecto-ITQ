package edu.valle.modules.reports.service.impl;

import edu.valle.common.enums.SaleStatus;
import edu.valle.exception.BusinessException;
import edu.valle.modules.inventory.repository.InventoryRepository;
import edu.valle.modules.payments.repository.PaymentRepository;
import edu.valle.modules.reports.dto.response.BestSellingProductResponse;
import edu.valle.modules.reports.dto.response.CategorySalesResponse;
import edu.valle.modules.reports.dto.response.DailyIncomeResponse;
import edu.valle.modules.reports.dto.response.IncomeRangeResponse;
import edu.valle.modules.reports.dto.response.LowStockReportResponse;
import edu.valle.modules.reports.dto.response.MonthlyIncomeResponse;
import edu.valle.modules.reports.dto.response.MonthlySalesResponse;
import edu.valle.modules.reports.dto.response.MonthlySalesTotalResponse;
import edu.valle.modules.reports.dto.response.PaymentMethodSummaryResponse;
import edu.valle.modules.reports.dto.response.TopProductResponse;
import edu.valle.modules.reports.dto.response.UserSalesResponse;
import edu.valle.modules.reports.service.ReportService;
import edu.valle.modules.sales.repository.SaleItemRepository;
import edu.valle.modules.sales.repository.SaleRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final PaymentRepository paymentRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional(readOnly = true)
    public MonthlyIncomeResponse getCurrentMonthIncome() {
        DateRange range = currentMonthRange();
        BigDecimal income = paymentRepository.sumAmountByPaidAtBetween(range.start(), range.end());
        return new MonthlyIncomeResponse(YearMonth.now(), money(income));
    }

    @Override
    @Transactional(readOnly = true)
    public MonthlySalesTotalResponse getCurrentMonthSalesTotal() {
        DateRange range = currentMonthRange();
        long totalSales = saleRepository.countBySaleDateBetweenAndStatusNot(
                range.start(),
                range.end(),
                SaleStatus.CANCELLED
        );
        return new MonthlySalesTotalResponse(YearMonth.now(), totalSales);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopProductResponse> getTopProductsCurrentMonth() {
        DateRange range = currentMonthRange();
        return saleItemRepository.findTopProducts(
                range.start(),
                range.end(),
                SaleStatus.CANCELLED
        );
    }

    @Override
    @Transactional(readOnly = true)
    public IncomeRangeResponse getIncomeByRange(LocalDateTime from, LocalDateTime to) {
        validateDateRange(from, to);
        BigDecimal totalIncome = paymentRepository.sumAmountByPaidAtBetween(from, to);
        return new IncomeRangeResponse(from, to, money(totalIncome));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyIncomeResponse> getDailyIncome(LocalDateTime from, LocalDateTime to) {
        validateDateRange(from, to);
        return paymentRepository.findDailyIncome(from, to).stream()
                .map(row -> new DailyIncomeResponse(toLocalDate(row[0]), money(row[1])))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlySalesResponse> getMonthlySales(YearMonth from, YearMonth to) {
        validateMonthRange(from, to);
        LocalDateTime start = from.atDay(1).atStartOfDay();
        LocalDateTime end = to.atEndOfMonth().atTime(LocalTime.MAX);
        return saleRepository.findMonthlySales(start, end).stream()
                .map(row -> new MonthlySalesResponse(
                        toYearMonth(row[0]),
                        ((Number) row[1]).longValue(),
                        money(row[2])
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BestSellingProductResponse> getTopProducts(
            LocalDateTime from,
            LocalDateTime to,
            int limit
    ) {
        validateDateRange(from, to);
        if (limit <= 0) {
            throw new BusinessException("Limit must be greater than zero");
        }
        return saleItemRepository.findBestSellingProducts(from, to, SaleStatus.CANCELLED).stream()
                .limit(limit)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LowStockReportResponse> getLowStockProducts() {
        return inventoryRepository.findLowStockReport();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSalesResponse> getSalesByUser(LocalDateTime from, LocalDateTime to) {
        validateDateRange(from, to);
        return saleRepository.summarizeSalesByUser(from, to, SaleStatus.CANCELLED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentMethodSummaryResponse> getPaymentsByMethod(LocalDateTime from, LocalDateTime to) {
        validateDateRange(from, to);
        return paymentRepository.summarizeByPaymentMethod(from, to);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorySalesResponse> getSalesByCategory(LocalDateTime from, LocalDateTime to) {
        validateDateRange(from, to);
        return saleItemRepository.summarizeSalesByCategory(from, to, SaleStatus.CANCELLED);
    }

    private DateRange currentMonthRange() {
        YearMonth currentMonth = YearMonth.now();
        return new DateRange(
                currentMonth.atDay(1).atStartOfDay(),
                currentMonth.atEndOfMonth().atTime(LocalTime.MAX)
        );
    }

    private void validateDateRange(LocalDateTime from, LocalDateTime to) {
        if (from.isAfter(to)) {
            throw new BusinessException("The start date cannot be after the end date");
        }
    }

    private void validateMonthRange(YearMonth from, YearMonth to) {
        if (from.isAfter(to)) {
            throw new BusinessException("The start month cannot be after the end month");
        }
    }

    private BigDecimal money(Object value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal amount = value instanceof BigDecimal decimal
                ? decimal
                : new BigDecimal(value.toString());
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private LocalDate toLocalDate(Object value) {
        if (value instanceof LocalDate date) {
            return date;
        }
        if (value instanceof java.sql.Date date) {
            return date.toLocalDate();
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime().toLocalDate();
        }
        return LocalDate.parse(value.toString());
    }

    private YearMonth toYearMonth(Object value) {
        if (value instanceof LocalDateTime dateTime) {
            return YearMonth.from(dateTime);
        }
        if (value instanceof Timestamp timestamp) {
            return YearMonth.from(timestamp.toLocalDateTime());
        }
        if (value instanceof LocalDate date) {
            return YearMonth.from(date);
        }
        return YearMonth.from(LocalDateTime.parse(value.toString().replace(' ', 'T')));
    }

    private record DateRange(LocalDateTime start, LocalDateTime end) {
    }
}
