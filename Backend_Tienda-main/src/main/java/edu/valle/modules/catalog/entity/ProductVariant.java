package edu.valle.modules.catalog.entity;
import edu.valle.shared.entity.BaseEntity;
import jakarta.persistence.*;
@Entity
@Table(name="product_variants", uniqueConstraints={
 @UniqueConstraint(name="uk_product_variant_sku",columnNames="sku"),
 @UniqueConstraint(name="uk_product_variant_product_size_color",columnNames={"product_id","size","color"})})
public class ProductVariant extends BaseEntity {
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_id",nullable=false) private Product product;
 @Column(nullable=false,length=30) private String size;
 @Column(nullable=false,length=50) private String color;
 @Column(nullable=false,length=80) private String sku;
 @Column(nullable=false) private Boolean active=true;
 public Product getProduct(){return product;} public void setProduct(Product v){product=v;}
 public String getSize(){return size;} public void setSize(String v){size=v;}
 public String getColor(){return color;} public void setColor(String v){color=v;}
 public String getSku(){return sku;} public void setSku(String v){sku=v;}
 public Boolean getActive(){return active;} public void setActive(Boolean v){active=v;}
}
