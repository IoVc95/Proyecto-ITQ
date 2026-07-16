package edu.valle.modules.inventory.entity;
import edu.valle.modules.catalog.entity.ProductVariant; import edu.valle.shared.entity.BaseEntity; import jakarta.persistence.*;
@Entity @Table(name="inventories") public class Inventory extends BaseEntity {
 @OneToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_variant_id",nullable=false,unique=true) private ProductVariant productVariant;
 @Column(name="current_stock",nullable=false) private Integer currentStock=0;
 @Column(name="min_stock",nullable=false) private Integer minStock=0;
 @Column(name="max_stock") private Integer maxStock;
 public ProductVariant getProductVariant(){return productVariant;} public void setProductVariant(ProductVariant v){productVariant=v;}
 public Integer getCurrentStock(){return currentStock;} public void setCurrentStock(Integer v){currentStock=v;}
 public Integer getMinStock(){return minStock;} public void setMinStock(Integer v){minStock=v;}
 public Integer getMaxStock(){return maxStock;} public void setMaxStock(Integer v){maxStock=v;}
}
