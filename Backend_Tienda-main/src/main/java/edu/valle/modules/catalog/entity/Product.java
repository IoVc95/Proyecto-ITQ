package edu.valle.modules.catalog.entity;

import edu.valle.shared.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product extends BaseEntity {
    @Column(nullable = false, length = 150) private String name;
    @Column(length = 700) private String description;
    @Column(name = "sale_price", nullable = false, precision = 12, scale = 2) private BigDecimal salePrice;
    @Column(name = "cost_price", nullable = false, precision = 12, scale = 2) private BigDecimal costPrice;
    @Column(nullable = false) private Boolean active = true;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "category_id", nullable = false) private Category category;
    public String getName(){return name;} public void setName(String value){name=value;}
    public String getDescription(){return description;} public void setDescription(String value){description=value;}
    public BigDecimal getSalePrice(){return salePrice;} public void setSalePrice(BigDecimal value){salePrice=value;}
    public BigDecimal getCostPrice(){return costPrice;} public void setCostPrice(BigDecimal value){costPrice=value;}
    public Boolean getActive(){return active;} public void setActive(Boolean value){active=value;}
    public Category getCategory(){return category;} public void setCategory(Category value){category=value;}
}
