package edu.valle.modules.sales.dto.response;

import edu.valle.common.enums.PaymentMethod;
import edu.valle.common.enums.SaleStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SaleResponse(
        Long id,
        String saleNumber,
        Long userId,
        String username,
        LocalDateTime saleDate,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal discount,
        BigDecimal total,
        SaleStatus status,
        PaymentMethod paymentMethod,
        BigDecimal paidAmount,
        List<SaleItemResponse> items
) {
}