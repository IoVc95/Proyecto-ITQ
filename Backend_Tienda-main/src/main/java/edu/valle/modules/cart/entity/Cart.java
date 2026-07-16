package edu.valle.modules.cart.entity;
import edu.valle.modules.users.entity.User; import edu.valle.shared.entity.BaseEntity; import jakarta.persistence.*; import java.util.*;
@Entity @Table(name="carts") public class Cart extends BaseEntity {
 @OneToOne(fetch=FetchType.LAZY) @JoinColumn(name="customer_id",nullable=false,unique=true) private User customer;
 @OneToMany(mappedBy="cart",cascade=CascadeType.ALL,orphanRemoval=true) private List<CartItem> items=new ArrayList<>();
 public User getCustomer(){return customer;} public void setCustomer(User v){customer=v;} public List<CartItem> getItems(){return items;} public void setItems(List<CartItem> v){items=v;}
 public void addItem(CartItem item){item.setCart(this);items.add(item);} public void removeItem(CartItem item){items.remove(item);item.setCart(null);} public void clear(){items.forEach(i->i.setCart(null));items.clear();}
}
