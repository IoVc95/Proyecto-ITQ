package edu.valle.modules.payments.service;

import edu.valle.modules.payments.dto.request.PaymentRequest;
import edu.valle.modules.payments.dto.response.PaymentResponse;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {

    List<PaymentResponse> findAll();

    PaymentResponse findById(Long id);

    List<PaymentResponse> findBySaleId(Long saleId);

    List<PaymentResponse> findByDateRange(LocalDateTime from, LocalDateTime to);

    PaymentResponse addPayment(Long saleId, PaymentRequest request);
}
