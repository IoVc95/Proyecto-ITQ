package edu.valle.modules.inventory.entity;
import edu.valle.common.enums.StockMovementType; import edu.valle.modules.catalog.entity.ProductVariant; import edu.valle.modules.users.entity.User; import edu.valle.shared.entity.BaseEntity; import jakarta.persistence.*;
@Entity @Table(name="stock_movements") public class StockMovement extends BaseEntity {
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_variant_id",nullable=false) private ProductVariant productVariant;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id",nullable=false) private User user;
 @Enumerated(EnumType.STRING) @Column(name="movement_type",nullable=false,length=30) private StockMovementType movementType;
 @Column(nullable=false) private Integer quantity; @Column(name="previous_stock",nullable=false) private Integer previousStock;
 @Column(name="new_stock",nullable=false) private Integer newStock; @Column(length=500) private String reason;
 public ProductVariant getProductVariant(){return productVariant;} public void setProductVariant(ProductVariant v){productVariant=v;}
 public User getUser(){return user;} public void setUser(User v){user=v;} public StockMovementType getMovementType(){return movementType;} public void setMovementType(StockMovementType v){movementType=v;}
 public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){quantity=v;} public Integer getPreviousStock(){return previousStock;} public void setPreviousStock(Integer v){previousStock=v;}
 public Integer getNewStock(){return newStock;} public void setNewStock(Integer v){newStock=v;} public String getReason(){return reason;} public void setReason(String v){reason=v;}
}
