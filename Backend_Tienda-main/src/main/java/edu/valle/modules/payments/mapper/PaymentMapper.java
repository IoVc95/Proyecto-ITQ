package edu.valle.modules.payments.mapper;

import edu.valle.common.enums.SaleStatus;
import edu.valle.modules.payments.dto.response.PaymentResponse;
import edu.valle.modules.payments.entity.Payment;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "id", source = "payment.id")
    @Mapping(target = "saleId", source = "payment.sale.id")
    @Mapping(target = "amount", source = "payment.amount")
    @Mapping(target = "paymentMethod", source = "payment.paymentMethod")
    @Mapping(target = "paidAt", source = "payment.paidAt")
    @Mapping(target = "createdAt", source = "payment.createdAt")
    PaymentResponse toResponse(
            Payment payment,
            BigDecimal saleTotal,
            BigDecimal totalPaid,
            BigDecimal remainingAmount,
            SaleStatus saleStatus
    );
}
