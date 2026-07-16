package edu.valle.modules.sales.dto.request;

import edu.valle.common.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record CreateSaleRequest(
        @NotNull Long userId,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal discount,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal taxPercentage,
        @NotNull PaymentMethod paymentMethod,
        @DecimalMin(value = "0.0", inclusive = false) BigDecimal paidAmount,
        @NotEmpty List<@Valid CreateSaleItemRequest> items
) {
}