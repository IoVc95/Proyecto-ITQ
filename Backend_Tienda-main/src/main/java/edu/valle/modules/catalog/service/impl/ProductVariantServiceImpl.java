package edu.valle.modules.catalog.service.impl;
import edu.valle.exception.*;
import edu.valle.modules.catalog.dto.request.ProductVariantRequest;
import edu.valle.modules.catalog.dto.response.ProductVariantResponse;
import edu.valle.modules.catalog.entity.*;
import edu.valle.modules.catalog.mapper.ProductVariantMapper;
import edu.valle.modules.catalog.repository.*;
import edu.valle.modules.catalog.service.ProductVariantService;
import edu.valle.modules.inventory.entity.Inventory;
import edu.valle.modules.inventory.repository.InventoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {
 private final ProductVariantRepository repository; private final ProductRepository productRepository;
 private final InventoryRepository inventoryRepository; private final ProductVariantMapper mapper;
 @Transactional public ProductVariantResponse create(ProductVariantRequest r){Product p=findProduct(r.productId());String s=norm(r.size()),c=norm(r.color()),sku=r.sku().trim();validateUnique(null,p.getId(),s,c,sku);ProductVariant v=mapper.toEntity(r);v.setProduct(p);v.setSize(s);v.setColor(c);v.setSku(sku);if(v.getActive()==null)v.setActive(true);v=repository.save(v);Inventory i=new Inventory();i.setProductVariant(v);inventoryRepository.save(i);return mapper.toResponse(v);}
 @Transactional(readOnly=true) public List<ProductVariantResponse> findAll(){return repository.findAll().stream().map(mapper::toResponse).toList();}
 @Transactional(readOnly=true) public ProductVariantResponse findById(Long id){return mapper.toResponse(find(id));}
 @Transactional(readOnly=true) public List<ProductVariantResponse> findByProductId(Long id){findProduct(id);return repository.findByProductId(id).stream().map(mapper::toResponse).toList();}
 @Transactional public ProductVariantResponse update(Long id,ProductVariantRequest r){ProductVariant v=find(id);Product p=findProduct(r.productId());String s=norm(r.size()),c=norm(r.color()),sku=r.sku().trim();validateUnique(id,p.getId(),s,c,sku);mapper.update(r,v);v.setProduct(p);v.setSize(s);v.setColor(c);v.setSku(sku);return mapper.toResponse(repository.save(v));}
 @Transactional public void deactivate(Long id){ProductVariant v=find(id);v.setActive(false);repository.save(v);}
 private void validateUnique(Long id,Long productId,String size,String color,String sku){repository.findBySkuIgnoreCase(sku).filter(v->!v.getId().equals(id)).ifPresent(v->{throw new BusinessException("Variant SKU already exists");});repository.findByProductId(productId).stream().filter(v->!v.getId().equals(id)).filter(v->norm(v.getSize()).equals(size)&&norm(v.getColor()).equals(color)).findAny().ifPresent(v->{throw new BusinessException("Product variant size and color already exist");});}
 private String norm(String v){return v.trim().replaceAll("\\s+"," ").toUpperCase(java.util.Locale.ROOT);}
 private ProductVariant find(Long id){return repository.findById(id).orElseThrow(()->new ResourceNotFoundException("ProductVariant",id));}
 private Product findProduct(Long id){return productRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Product",id));}
}
