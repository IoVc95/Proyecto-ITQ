package edu.valle.modules.payments.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.valle.common.enums.PaymentMethod;
import edu.valle.common.enums.SaleStatus;
import edu.valle.exception.BusinessException;
import edu.valle.modules.payments.dto.request.PaymentRequest;
import edu.valle.modules.payments.dto.response.PaymentResponse;
import edu.valle.modules.payments.entity.Payment;
import edu.valle.modules.payments.mapper.PaymentMapper;
import edu.valle.modules.payments.repository.PaymentRepository;
import edu.valle.modules.sales.entity.Sale;
import edu.valle.modules.sales.repository.SaleRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private PaymentMapper paymentMapper;

    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(paymentRepository, saleRepository, paymentMapper);
    }

    @Test
    void partialPaymentKeepsSalePending() {
        Sale sale = sale("50.00", SaleStatus.PENDING);
        PaymentRequest request = new PaymentRequest(new BigDecimal("10.00"), PaymentMethod.CASH);
        PaymentResponse expected = response("30.00", "20.00", SaleStatus.PENDING);
        when(saleRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sale));
        when(paymentRepository.sumAmountBySaleId(1L)).thenReturn(new BigDecimal("20.00"));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentMapper.toResponse(
                any(Payment.class),
                eq(new BigDecimal("50.00")),
                eq(new BigDecimal("30.00")),
                eq(new BigDecimal("20.00")),
                eq(SaleStatus.PENDING)
        )).thenReturn(expected);

        PaymentResponse result = paymentService.addPayment(1L, request);

        assertEquals(expected, result);
        assertEquals(SaleStatus.PENDING, sale.getStatus());
    }

    @Test
    void finalPaymentMarksSalePaid() {
        Sale sale = sale("50.00", SaleStatus.PENDING);
        PaymentRequest request = new PaymentRequest(new BigDecimal("30.00"), PaymentMethod.TRANSFER);
        when(saleRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sale));
        when(paymentRepository.sumAmountBySaleId(1L)).thenReturn(new BigDecimal("20.00"));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        paymentService.addPayment(1L, request);

        assertEquals(SaleStatus.PAID, sale.getStatus());
        verify(saleRepository).save(sale);
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        assertEquals(new BigDecimal("30.00"), captor.getValue().getAmount());
    }

    @Test
    void paymentCannotExceedRemainingBalance() {
        Sale sale = sale("50.00", SaleStatus.PENDING);
        when(saleRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sale));
        when(paymentRepository.sumAmountBySaleId(1L)).thenReturn(new BigDecimal("20.00"));

        assertThrows(BusinessException.class, () -> paymentService.addPayment(
                1L,
                new PaymentRequest(new BigDecimal("31.00"), PaymentMethod.CASH)
        ));

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void cannotAddPaymentToPaidSale() {
        Sale sale = sale("50.00", SaleStatus.PAID);
        when(saleRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sale));

        assertThrows(BusinessException.class, () -> paymentService.addPayment(
                1L,
                new PaymentRequest(new BigDecimal("1.00"), PaymentMethod.CASH)
        ));
    }

    @Test
    void cannotAddPaymentToCancelledSale() {
        Sale sale = sale("50.00", SaleStatus.CANCELLED);
        when(saleRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sale));

        assertThrows(BusinessException.class, () -> paymentService.addPayment(
                1L,
                new PaymentRequest(new BigDecimal("1.00"), PaymentMethod.CASH)
        ));
    }

    @Test
    void rejectsInvalidDateRange() {
        assertThrows(BusinessException.class, () -> paymentService.findByDateRange(
                java.time.LocalDateTime.of(2026, 6, 30, 0, 0),
                java.time.LocalDateTime.of(2026, 6, 1, 0, 0)
        ));
    }

    private Sale sale(String total, SaleStatus status) {
        Sale sale = new Sale();
        sale.setId(1L);
        sale.setTotal(new BigDecimal(total));
        sale.setStatus(status);
        return sale;
    }

    private PaymentResponse response(String totalPaid, String remaining, SaleStatus status) {
        return new PaymentResponse(
                1L,
                1L,
                new BigDecimal("10.00"),
                PaymentMethod.CASH,
                null,
                null,
                new BigDecimal("50.00"),
                new BigDecimal(totalPaid),
                new BigDecimal(remaining),
                status
        );
    }
}
