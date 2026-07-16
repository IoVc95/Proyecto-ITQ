package edu.valle.modules.reports.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.valle.common.enums.SaleStatus;
import edu.valle.exception.BusinessException;
import edu.valle.modules.inventory.repository.InventoryRepository;
import edu.valle.modules.payments.repository.PaymentRepository;
import edu.valle.modules.reports.dto.response.BestSellingProductResponse;
import edu.valle.modules.reports.dto.response.IncomeRangeResponse;
import edu.valle.modules.reports.dto.response.MonthlyIncomeResponse;
import edu.valle.modules.sales.repository.SaleItemRepository;
import edu.valle.modules.sales.repository.SaleRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private SaleItemRepository saleItemRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    private ReportServiceImpl reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportServiceImpl(
                saleRepository,
                saleItemRepository,
                paymentRepository,
                inventoryRepository
        );
    }

    @Test
    void currentMonthIncomeUsesPaymentsInsteadOfSales() {
        when(paymentRepository.sumAmountByPaidAtBetween(any(), any()))
                .thenReturn(new BigDecimal("125.50"));

        MonthlyIncomeResponse response = reportService.getCurrentMonthIncome();

        assertEquals(new BigDecimal("125.50"), response.income());
        verify(saleRepository, never()).sumTotalBySaleDateBetween(any(), any());
    }

    @Test
    void currentMonthSalesExcludeCancelledSales() {
        when(saleRepository.countBySaleDateBetweenAndStatusNot(
                any(),
                any(),
                eq(SaleStatus.CANCELLED)
        )).thenReturn(4L);

        assertEquals(4L, reportService.getCurrentMonthSalesTotal().totalSales());
    }

    @Test
    void currentMonthTopProductsExcludeCancelledSales() {
        reportService.getTopProductsCurrentMonth();

        verify(saleItemRepository).findTopProducts(any(), any(), eq(SaleStatus.CANCELLED));
    }

    @Test
    void incomeRangeReturnsZeroWhenRepositoryHasNoData() {
        LocalDateTime from = LocalDateTime.of(2026, 6, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 6, 30, 23, 59, 59);
        when(paymentRepository.sumAmountByPaidAtBetween(from, to)).thenReturn(null);

        IncomeRangeResponse response = reportService.getIncomeByRange(from, to);

        assertEquals(new BigDecimal("0.00"), response.totalIncome());
    }

    @Test
    void topProductsAppliesRequestedLimit() {
        LocalDateTime from = LocalDateTime.of(2026, 6, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 6, 30, 23, 59, 59);
        when(saleItemRepository.findBestSellingProducts(from, to, SaleStatus.CANCELLED))
                .thenReturn(List.of(
                        product(1L),
                        product(2L),
                        product(3L)
                ));

        assertEquals(2, reportService.getTopProducts(from, to, 2).size());
    }

    @Test
    void invalidDateRangeIsRejected() {
        LocalDateTime from = LocalDateTime.of(2026, 6, 30, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 6, 1, 0, 0);

        assertThrows(BusinessException.class, () -> reportService.getIncomeByRange(from, to));
    }

    @Test
    void nonPositiveTopLimitIsRejected() {
        LocalDateTime from = LocalDateTime.of(2026, 6, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 6, 30, 0, 0);

        assertThrows(BusinessException.class, () -> reportService.getTopProducts(from, to, 0));
    }

    private BestSellingProductResponse product(Long id) {
        return new BestSellingProductResponse(
                id,
                "Product " + id,
                1L,
                BigDecimal.ONE
        );
    }
}
