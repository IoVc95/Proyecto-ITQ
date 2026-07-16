package edu.valle.modules.cart.repository;
import edu.valle.modules.cart.entity.CartItem; import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository;
public interface CartItemRepository extends JpaRepository<CartItem,Long>{Optional<CartItem> findByIdAndCartCustomerUsername(Long id,String username);Optional<CartItem> findByCartIdAndProductVariantId(Long cartId,Long variantId);}
