package edu.valle.modules.sales.entity;
import edu.valle.modules.catalog.entity.ProductVariant; import edu.valle.shared.entity.BaseEntity; import jakarta.persistence.*; import java.math.BigDecimal;
@Entity @Table(name="sale_items") public class SaleItem extends BaseEntity {
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="sale_id",nullable=false) private Sale sale;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_variant_id",nullable=false) private ProductVariant productVariant;
 @Column(nullable=false) private Integer quantity; @Column(name="unit_price",nullable=false,precision=12,scale=2) private BigDecimal unitPrice; @Column(nullable=false,precision=12,scale=2) private BigDecimal subtotal;
 public Sale getSale(){return sale;} public void setSale(Sale v){sale=v;} public ProductVariant getProductVariant(){return productVariant;} public void setProductVariant(ProductVariant v){productVariant=v;}
 public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){quantity=v;} public BigDecimal getUnitPrice(){return unitPrice;} public void setUnitPrice(BigDecimal v){unitPrice=v;} public BigDecimal getSubtotal(){return subtotal;} public void setSubtotal(BigDecimal v){subtotal=v;}
}
