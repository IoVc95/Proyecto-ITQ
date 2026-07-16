package edu.valle.modules.cart.service;
import edu.valle.modules.cart.dto.request.CartItemRequest; import edu.valle.modules.cart.dto.response.CartResponse;
public interface CartService {CartResponse get(String username);CartResponse add(String username,CartItemRequest request);CartResponse update(String username,Long itemId,CartItemRequest request);void remove(String username,Long itemId);void clear(String username);}
