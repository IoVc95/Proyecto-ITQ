package edu.valle.modules.cart.dto.response;
import java.math.BigDecimal;
public record CartItemResponse(Long id,Long productVariantId,Long productId,String productName,String size,String color,BigDecimal unitPrice,Integer quantity,BigDecimal subtotal,Integer availableStock) {}
