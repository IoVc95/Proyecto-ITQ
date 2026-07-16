package edu.valle.modules.cart.entity;
import edu.valle.modules.catalog.entity.ProductVariant; import edu.valle.shared.entity.BaseEntity; import jakarta.persistence.*;
@Entity @Table(name="cart_items",uniqueConstraints=@UniqueConstraint(name="uk_cart_item_variant",columnNames={"cart_id","product_variant_id"})) public class CartItem extends BaseEntity {
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="cart_id",nullable=false) private Cart cart;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_variant_id",nullable=false) private ProductVariant productVariant;
 @Column(nullable=false) private Integer quantity;
 public Cart getCart(){return cart;} public void setCart(Cart v){cart=v;} public ProductVariant getProductVariant(){return productVariant;} public void setProductVariant(ProductVariant v){productVariant=v;} public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){quantity=v;}
}
