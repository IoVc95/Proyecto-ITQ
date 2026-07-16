package edu.valle.modules.catalog.service;

import edu.valle.modules.catalog.dto.request.ProductRequest;
import edu.valle.modules.catalog.dto.response.ProductResponse;
import java.util.List;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    List<ProductResponse> findAll();

    ProductResponse findById(Long id);

    ProductResponse findByBarcode(String barcode);

    ProductResponse findByCode(String code);

    ProductResponse update(Long id, ProductRequest request);

    void deactivate(Long id);
}
