package edu.valle.modules.catalog.service.impl;

import edu.valle.exception.BusinessException;
import edu.valle.exception.ResourceNotFoundException;
import edu.valle.modules.catalog.dto.request.CategoryRequest;
import edu.valle.modules.catalog.dto.response.CategoryResponse;
import edu.valle.modules.catalog.entity.Category;
import edu.valle.modules.catalog.mapper.CategoryMapper;
import edu.valle.modules.catalog.repository.CategoryRepository;
import edu.valle.modules.catalog.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new BusinessException("Category name already exists");
        }
        Category category = categoryMapper.toEntity(request);
        if (category.getActive() == null) {
            category.setActive(true);
        }
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse findById(Long id) {
        return categoryMapper.toResponse(findEntityById(id));
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findEntityById(id);
        categoryRepository.findByNameIgnoreCase(request.name())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("Category name already exists");
                });
        categoryMapper.updateEntity(request, category);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public void deactivate(Long id) {
        Category category = findEntityById(id);
        category.setActive(false);
        categoryRepository.save(category);
    }

    private Category findEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }
}