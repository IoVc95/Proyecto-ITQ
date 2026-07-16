package edu.valle.modules.sales.service;

import edu.valle.modules.sales.dto.request.CreateSaleRequest;
import edu.valle.modules.sales.dto.request.CheckoutRequest;
import edu.valle.modules.sales.dto.request.UpdateSaleStatusRequest;
import edu.valle.modules.sales.dto.response.SaleResponse;
import java.util.List;

public interface SaleService {

    SaleResponse create(CreateSaleRequest request);

    SaleResponse findById(Long id);
    List<SaleResponse> findAll();
    SaleResponse checkout(String username, CheckoutRequest request);
    List<SaleResponse> findMyOrders(String username);
    SaleResponse findMyOrder(String username, Long id);
    SaleResponse updateStatus(Long id, UpdateSaleStatusRequest request);
}
