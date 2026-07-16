package edu.valle.modules.catalog.service.impl;
import static org.junit.jupiter.api.Assertions.assertSame; import static org.mockito.Mockito.*;
import edu.valle.modules.catalog.dto.request.ProductRequest; import edu.valle.modules.catalog.dto.response.ProductResponse; import edu.valle.modules.catalog.entity.*; import edu.valle.modules.catalog.mapper.ProductMapper; import edu.valle.modules.catalog.repository.*; import java.math.BigDecimal; import java.util.Optional; import org.junit.jupiter.api.*; import org.junit.jupiter.api.extension.ExtendWith; import org.mockito.*; import org.mockito.junit.jupiter.MockitoExtension;
@ExtendWith(MockitoExtension.class) class ProductServiceImplTest {
 @Mock ProductRepository products; @Mock CategoryRepository categories; @Mock ProductMapper mapper; ProductServiceImpl service;
 @BeforeEach void setup(){service=new ProductServiceImpl(products,categories,mapper);}
 @Test void createsClothingProduct(){ProductRequest r=new ProductRequest("Camiseta","Algodon",new BigDecimal("20"),new BigDecimal("10"),true,1L);Category c=new Category();c.setId(1L);c.setActive(true);Product p=new Product();ProductResponse expected=new ProductResponse(2L,"Camiseta","Algodon",new BigDecimal("20"),new BigDecimal("10"),true,1L,"Camisetas",null,null);when(categories.findById(1L)).thenReturn(Optional.of(c));when(mapper.toEntity(r)).thenReturn(p);when(products.save(p)).thenReturn(p);when(mapper.toResponse(p)).thenReturn(expected);assertSame(expected,service.create(r));verify(products).save(p);}
}
