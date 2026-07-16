package edu.valle.modules.sales.service;

import edu.valle.modules.sales.dto.request.CreateSaleRequest;
import edu.valle.modules.sales.dto.response.SaleResponse;

public interface SaleService {

    SaleResponse create(CreateSaleRequest request);

    SaleResponse findById(Long id);
}