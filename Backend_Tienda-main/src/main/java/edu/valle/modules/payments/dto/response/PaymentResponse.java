package edu.valle.modules.payments.dto.response;

import edu.valle.common.enums.PaymentMethod;
import edu.valle.common.enums.SaleStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long saleId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        LocalDateTime paidAt,
        LocalDateTime createdAt,
        BigDecimal saleTotal,
        BigDecimal totalPaid,
        BigDecimal remainingAmount,
        SaleStatus saleStatus
) {
}
