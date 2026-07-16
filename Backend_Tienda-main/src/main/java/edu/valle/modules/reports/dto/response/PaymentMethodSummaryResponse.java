package edu.valle.modules.reports.dto.response;

import edu.valle.common.enums.PaymentMethod;
import java.math.BigDecimal;

public record PaymentMethodSummaryResponse(
        PaymentMethod paymentMethod,
        Long paymentCount,
        BigDecimal totalAmount
) {
}
