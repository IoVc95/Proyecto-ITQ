package edu.valle.modules.catalog.service.impl;
import edu.valle.exception.*;
import edu.valle.modules.catalog.dto.request.ProductRequest;
import edu.valle.modules.catalog.dto.response.ProductResponse;
import edu.valle.modules.catalog.entity.*;
import edu.valle.modules.catalog.mapper.ProductMapper;
import edu.valle.modules.catalog.repository.*;
import edu.valle.modules.catalog.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    @Transactional public ProductResponse create(ProductRequest request){
        Product product=productMapper.toEntity(request); product.setCategory(findCategory(request.categoryId()));
        if(product.getActive()==null) product.setActive(true);
        return productMapper.toResponse(productRepository.save(product));
    }
    @Transactional(readOnly=true) public List<ProductResponse> findAll(){return productRepository.findAll().stream().map(productMapper::toResponse).toList();}
    @Transactional(readOnly=true) public ProductResponse findById(Long id){return productMapper.toResponse(findEntity(id));}
    @Transactional public ProductResponse update(Long id,ProductRequest request){Product p=findEntity(id); productMapper.updateEntity(request,p); p.setCategory(findCategory(request.categoryId())); return productMapper.toResponse(productRepository.save(p));}
    @Transactional public void deactivate(Long id){Product p=findEntity(id);p.setActive(false);productRepository.save(p);}
    private Product findEntity(Long id){return productRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Product",id));}
    private Category findCategory(Long id){Category c=categoryRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Category",id));if(Boolean.FALSE.equals(c.getActive()))throw new BusinessException("Category is inactive");return c;}
}
