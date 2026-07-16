package edu.valle.modules.sales.dto.request;
import edu.valle.common.enums.PaymentMethod; import jakarta.validation.constraints.NotNull;
public record CheckoutRequest(@NotNull PaymentMethod paymentMethod) {}
