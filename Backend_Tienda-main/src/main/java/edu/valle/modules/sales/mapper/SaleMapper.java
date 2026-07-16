package edu.valle.modules.sales.mapper;

import edu.valle.common.enums.PaymentMethod;
import edu.valle.modules.payments.entity.Payment;
import edu.valle.modules.sales.dto.response.SaleItemResponse;
import edu.valle.modules.sales.dto.response.SaleResponse;
import edu.valle.modules.sales.entity.Sale;
import edu.valle.modules.sales.entity.SaleItem;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaleMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    SaleItemResponse toItemResponse(SaleItem item);

    @Mapping(target = "id", source = "sale.id")
    @Mapping(target = "userId", source = "sale.user.id")
    @Mapping(target = "username", source = "sale.user.username")
    @Mapping(target = "paymentMethod", source = "payment.paymentMethod")
    @Mapping(target = "paidAmount", source = "totalPaid")
    SaleResponse toResponse(Sale sale, Payment payment, BigDecimal totalPaid);

    default PaymentMethod mapPaymentMethod(Payment payment) {
        return payment == null ? null : payment.getPaymentMethod();
    }

    default BigDecimal mapPaidAmount(Payment payment) {
        return payment == null ? null : payment.getAmount();
    }
}
