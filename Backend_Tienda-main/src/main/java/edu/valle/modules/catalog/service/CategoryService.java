package edu.valle.modules.catalog.service;

import edu.valle.modules.catalog.dto.request.CategoryRequest;
import edu.valle.modules.catalog.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);

    List<CategoryResponse> findAll();

    CategoryResponse findById(Long id);

    CategoryResponse update(Long id, CategoryRequest request);

    void deactivate(Long id);
}