package edu.valle.modules.sales.service.impl;

import edu.valle.common.enums.*;
import edu.valle.exception.*;
import edu.valle.modules.cart.entity.Cart;
import edu.valle.modules.cart.repository.CartRepository;
import edu.valle.modules.catalog.entity.*;
import edu.valle.modules.catalog.repository.ProductVariantRepository;
import edu.valle.modules.inventory.entity.*;
import edu.valle.modules.inventory.repository.*;
import edu.valle.modules.payments.entity.Payment;
import edu.valle.modules.payments.repository.PaymentRepository;
import edu.valle.modules.sales.dto.request.*;
import edu.valle.modules.sales.dto.response.SaleResponse;
import edu.valle.modules.sales.entity.*;
import edu.valle.modules.sales.mapper.SaleMapper;
import edu.valle.modules.sales.repository.SaleRepository;
import edu.valle.modules.sales.service.SaleService;
import edu.valle.modules.users.entity.User;
import edu.valle.modules.users.repository.UserRepository;
import java.math.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {
 private static final BigDecimal HUNDRED=new BigDecimal("100");
 private final SaleRepository sales; private final ProductVariantRepository variants; private final InventoryRepository inventories;
 private final StockMovementRepository movements; private final PaymentRepository payments; private final UserRepository users;
 private final CartRepository carts; private final SaleMapper mapper;

 @Transactional public SaleResponse create(CreateSaleRequest r){
  User user=users.findById(r.userId()).orElseThrow(()->new ResourceNotFoundException("User",r.userId()));
  Sale sale=baseSale(user); BigDecimal subtotal=BigDecimal.ZERO;
  for(CreateSaleItemRequest item:r.items()) subtotal=subtotal.add(addItem(sale,user,item.productVariantId(),item.quantity()));
  BigDecimal discount=money(zero(r.discount())); if(discount.compareTo(subtotal)>0)throw new BusinessException("Discount cannot be greater than subtotal");
  BigDecimal taxable=subtotal.subtract(discount); BigDecimal tax=taxable.multiply(zero(r.taxPercentage())).divide(HUNDRED,2,RoundingMode.HALF_UP); BigDecimal total=taxable.add(tax).setScale(2,RoundingMode.HALF_UP);
  finishAmounts(sale,subtotal,discount,tax,total); BigDecimal paid=r.paidAmount()==null?total:money(r.paidAmount()); if(paid.compareTo(total)>0)throw new BusinessException("Paid amount cannot be greater than sale total"); sale.setStatus(paid.compareTo(total)>=0?SaleStatus.PAID:SaleStatus.PENDING);
  Sale saved=sales.save(sale); Payment payment=payment(saved,r.paymentMethod(),paid); return mapper.toResponse(saved,payment,paid);
 }

 @Transactional public SaleResponse checkout(String username,CheckoutRequest r){
  User customer=customer(username); Cart cart=carts.findByCustomerUsername(username).orElseThrow(()->new BusinessException("Cart is empty")); if(cart.getItems().isEmpty())throw new BusinessException("Cart is empty");
  Sale sale=baseSale(customer); BigDecimal subtotal=BigDecimal.ZERO;
  for(var cartItem:new ArrayList<>(cart.getItems())) subtotal=subtotal.add(addItem(sale,customer,cartItem.getProductVariant().getId(),cartItem.getQuantity()));
  subtotal=money(subtotal); finishAmounts(sale,subtotal,BigDecimal.ZERO.setScale(2),BigDecimal.ZERO.setScale(2),subtotal); sale.setStatus(SaleStatus.PAID);
  Sale saved=sales.save(sale); Payment payment=payment(saved,r.paymentMethod(),subtotal); cart.clear(); carts.save(cart); return mapper.toResponse(saved,payment,subtotal);
 }

 @Transactional(readOnly=true) public List<SaleResponse> findAll(){return sales.findAllByOrderBySaleDateDesc().stream().map(this::response).toList();}
 @Transactional(readOnly=true) public SaleResponse findById(Long id){return response(sales.findById(id).orElseThrow(()->new ResourceNotFoundException("Sale",id)));}
 @Transactional(readOnly=true) public List<SaleResponse> findMyOrders(String username){customer(username);return sales.findByUserUsernameOrderBySaleDateDesc(username).stream().map(this::response).toList();}
 @Transactional(readOnly=true) public SaleResponse findMyOrder(String username,Long id){customer(username);return response(sales.findByIdAndUserUsername(id,username).orElseThrow(()->new ResourceNotFoundException("Sale",id)));}

 @Transactional public SaleResponse updateStatus(Long id,UpdateSaleStatusRequest r){
  Sale sale=sales.findByIdForUpdate(id).orElseThrow(()->new ResourceNotFoundException("Sale",id)); SaleStatus current=sale.getStatus(),next=r.status();
  boolean allowed=(current==SaleStatus.PAID&&(next==SaleStatus.PREPARING||next==SaleStatus.CANCELLED))||(current==SaleStatus.PREPARING&&(next==SaleStatus.DELIVERED||next==SaleStatus.CANCELLED));
  if(!allowed)throw new BusinessException("Invalid sale status transition: "+current+" -> "+next);
  if(next==SaleStatus.CANCELLED)restoreStock(sale); sale.setStatus(next); return response(sales.save(sale));
 }

 private BigDecimal addItem(Sale sale,User user,Long variantId,int quantity){
  ProductVariant v=variants.findById(variantId).orElseThrow(()->new ResourceNotFoundException("ProductVariant",variantId)); Product p=v.getProduct();
  if(Boolean.FALSE.equals(v.getActive()))throw new BusinessException("Product variant is inactive: "+v.getSku()); if(Boolean.FALSE.equals(p.getActive()))throw new BusinessException("Product is inactive: "+p.getName());
  Inventory inventory=inventories.findByProductVariantIdForUpdate(v.getId()).orElseThrow(()->new ResourceNotFoundException("Inventory not found for product variant id: "+v.getId())); if(inventory.getCurrentStock()<quantity)throw new BusinessException("Insufficient stock for product variant: "+v.getSku());
  BigDecimal subtotal=p.getSalePrice().multiply(BigDecimal.valueOf(quantity)).setScale(2,RoundingMode.HALF_UP); SaleItem item=new SaleItem();item.setSale(sale);item.setProductVariant(v);item.setQuantity(quantity);item.setUnitPrice(p.getSalePrice());item.setSubtotal(subtotal);sale.getItems().add(item);
  int previous=inventory.getCurrentStock(),next=previous-quantity; inventory.setCurrentStock(next);inventories.save(inventory);movement(v,user,StockMovementType.SALE,quantity,previous,next,"Sale stock deduction"); return subtotal;
 }
 private void restoreStock(Sale sale){for(SaleItem item:sale.getItems()){ProductVariant v=item.getProductVariant();Inventory i=inventories.findByProductVariantIdForUpdate(v.getId()).orElseThrow(()->new ResourceNotFoundException("Inventory not found for product variant id: "+v.getId()));int previous=i.getCurrentStock(),next=previous+item.getQuantity();i.setCurrentStock(next);inventories.save(i);movement(v,sale.getUser(),StockMovementType.IN,item.getQuantity(),previous,next,"Stock restored by sale cancellation");}}
 private Sale baseSale(User user){Sale s=new Sale();s.setSaleNumber(number());s.setUser(user);s.setSaleDate(LocalDateTime.now());s.setSubtotal(BigDecimal.ZERO);s.setDiscount(BigDecimal.ZERO);s.setTax(BigDecimal.ZERO);s.setTotal(BigDecimal.ZERO);s.setStatus(SaleStatus.PENDING);return s;}
 private void finishAmounts(Sale s,BigDecimal subtotal,BigDecimal discount,BigDecimal tax,BigDecimal total){s.setSubtotal(money(subtotal));s.setDiscount(money(discount));s.setTax(money(tax));s.setTotal(money(total));}
 private Payment payment(Sale sale,PaymentMethod method,BigDecimal amount){Payment p=new Payment();p.setSale(sale);p.setPaymentMethod(method);p.setAmount(money(amount));p.setPaidAt(LocalDateTime.now());return payments.save(p);}
 private void movement(ProductVariant v,User u,StockMovementType type,int quantity,int previous,int next,String reason){StockMovement m=new StockMovement();m.setProductVariant(v);m.setUser(u);m.setMovementType(type);m.setQuantity(quantity);m.setPreviousStock(previous);m.setNewStock(next);m.setReason(reason);movements.save(m);}
 private User customer(String username){User u=users.findByUsername(username).orElseThrow(()->new ResourceNotFoundException("User not found"));if(Boolean.FALSE.equals(u.getActive())||u.getRole()!=UserRole.CUSTOMER)throw new BusinessException("Active CUSTOMER role is required");return u;}
 private SaleResponse response(Sale sale){List<Payment> list=payments.findBySaleIdOrderByPaidAtAsc(sale.getId());Payment last=list.isEmpty()?null:list.get(list.size()-1);BigDecimal paid=list.stream().map(Payment::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);return mapper.toResponse(sale,last,money(paid));}
 private BigDecimal zero(BigDecimal v){return v==null?BigDecimal.ZERO:v;} private BigDecimal money(BigDecimal v){return v.setScale(2,RoundingMode.HALF_UP);} private String number(){return "SALE-"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))+"-"+UUID.randomUUID().toString().substring(0,8).toUpperCase();}
}
