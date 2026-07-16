package edu.valle.modules.inventory.repository;

import edu.valle.common.enums.StockMovementType;
import edu.valle.modules.inventory.entity.StockMovement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByProductVariantId(Long productVariantId);

    List<StockMovement> findByUserId(Long userId);

    List<StockMovement> findByMovementType(StockMovementType movementType);
}
