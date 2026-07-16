package edu.valle.modules.payments.service.impl;

import edu.valle.common.enums.SaleStatus;
import edu.valle.exception.BusinessException;
import edu.valle.exception.ResourceNotFoundException;
import edu.valle.modules.payments.dto.request.PaymentRequest;
import edu.valle.modules.payments.dto.response.PaymentResponse;
import edu.valle.modules.payments.entity.Payment;
import edu.valle.modules.payments.mapper.PaymentMapper;
import edu.valle.modules.payments.repository.PaymentRepository;
import edu.valle.modules.payments.service.PaymentService;
import edu.valle.modules.sales.entity.Sale;
import edu.valle.modules.sales.repository.SaleRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final SaleRepository saleRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> findAll() {
        return paymentRepository.findAllByOrderByPaidAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse findById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
        return toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> findBySaleId(Long saleId) {
        ensureSaleExists(saleId);
        return paymentRepository.findBySaleIdOrderByPaidAtAsc(saleId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> findByDateRange(LocalDateTime from, LocalDateTime to) {
        if (from.isAfter(to)) {
            throw new BusinessException("The start date cannot be after the end date");
        }
        return paymentRepository.findByPaidAtBetweenOrderByPaidAtAsc(from, to).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PaymentResponse addPayment(Long saleId, PaymentRequest request) {
        Sale sale = saleRepository.findByIdForUpdate(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", saleId));

        if (sale.getStatus() == SaleStatus.CANCELLED) {
            throw new BusinessException("Cannot add payments to a cancelled sale");
        }
        if (sale.getStatus() == SaleStatus.PAID) {
            throw new BusinessException("The sale is already fully paid");
        }

        BigDecimal saleTotal = money(sale.getTotal());
        BigDecimal currentTotalPaid = totalPaid(saleId);
        BigDecimal remainingAmount = saleTotal.subtract(currentTotalPaid).max(BigDecimal.ZERO);
        BigDecimal amount = money(request.amount());

        if (amount.compareTo(remainingAmount) > 0) {
            throw new BusinessException("Payment amount cannot exceed the remaining sale balance");
        }

        Payment payment = new Payment();
        payment.setSale(sale);
        payment.setAmount(amount);
        payment.setPaymentMethod(request.paymentMethod());
        payment.setPaidAt(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);

        BigDecimal newTotalPaid = currentTotalPaid.add(amount).setScale(2, RoundingMode.HALF_UP);
        SaleStatus newStatus = newTotalPaid.compareTo(saleTotal) >= 0
                ? SaleStatus.PAID
                : SaleStatus.PENDING;
        sale.setStatus(newStatus);
        saleRepository.save(sale);

        return paymentMapper.toResponse(
                savedPayment,
                saleTotal,
                newTotalPaid,
                saleTotal.subtract(newTotalPaid).max(BigDecimal.ZERO),
                newStatus
        );
    }

    private PaymentResponse toResponse(Payment payment) {
        Sale sale = payment.getSale();
        BigDecimal saleTotal = money(sale.getTotal());
        BigDecimal totalPaid = totalPaid(sale.getId());
        return paymentMapper.toResponse(
                payment,
                saleTotal,
                totalPaid,
                saleTotal.subtract(totalPaid).max(BigDecimal.ZERO),
                sale.getStatus()
        );
    }

    private BigDecimal totalPaid(Long saleId) {
        BigDecimal total = paymentRepository.sumAmountBySaleId(saleId);
        return money(total == null ? BigDecimal.ZERO : total);
    }

    private void ensureSaleExists(Long saleId) {
        if (!saleRepository.existsById(saleId)) {
            throw new ResourceNotFoundException("Sale", saleId);
        }
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
