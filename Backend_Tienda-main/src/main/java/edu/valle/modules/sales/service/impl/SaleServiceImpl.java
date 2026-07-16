package edu.valle.modules.sales.service.impl;

import edu.valle.common.enums.SaleStatus;
import edu.valle.common.enums.StockMovementType;
import edu.valle.exception.BusinessException;
import edu.valle.exception.ResourceNotFoundException;
import edu.valle.modules.catalog.entity.Product;
import edu.valle.modules.catalog.repository.ProductRepository;
import edu.valle.modules.inventory.entity.Inventory;
import edu.valle.modules.inventory.entity.StockMovement;
import edu.valle.modules.inventory.repository.InventoryRepository;
import edu.valle.modules.inventory.repository.StockMovementRepository;
import edu.valle.modules.payments.entity.Payment;
import edu.valle.modules.payments.repository.PaymentRepository;
import edu.valle.modules.sales.dto.request.CreateSaleItemRequest;
import edu.valle.modules.sales.dto.request.CreateSaleRequest;
import edu.valle.modules.sales.dto.response.SaleResponse;
import edu.valle.modules.sales.entity.Sale;
import edu.valle.modules.sales.entity.SaleItem;
import edu.valle.modules.sales.mapper.SaleMapper;
import edu.valle.modules.sales.repository.SaleRepository;
import edu.valle.modules.sales.service.SaleService;
import edu.valle.modules.users.entity.User;
import edu.valle.modules.users.repository.UserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final SaleMapper saleMapper;

    @Override
    @Transactional
    public SaleResponse create(CreateSaleRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.userId()));

        Sale sale = new Sale();
        sale.setSaleNumber(generateSaleNumber());
        sale.setUser(user);
        sale.setSaleDate(LocalDateTime.now());
        sale.setDiscount(valueOrZero(request.discount()));
        sale.setTax(BigDecimal.ZERO);
        sale.setSubtotal(BigDecimal.ZERO);
        sale.setTotal(BigDecimal.ZERO);
        sale.setStatus(SaleStatus.PENDING);

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CreateSaleItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", itemRequest.productId()));
            if (Boolean.FALSE.equals(product.getActive())) {
                throw new BusinessException("Product is inactive: " + product.getName());
            }
            Inventory inventory = inventoryRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + product.getId()));
            if (inventory.getCurrentStock() < itemRequest.quantity()) {
                throw new BusinessException("Insufficient stock for product: " + product.getName());
            }

            BigDecimal itemSubtotal = product.getSalePrice()
                    .multiply(BigDecimal.valueOf(itemRequest.quantity()))
                    .setScale(2, RoundingMode.HALF_UP);

            SaleItem saleItem = new SaleItem();
            saleItem.setSale(sale);
            saleItem.setProduct(product);
            saleItem.setQuantity(itemRequest.quantity());
            saleItem.setUnitPrice(product.getSalePrice());
            saleItem.setSubtotal(itemSubtotal);
            sale.getItems().add(saleItem);

            int previousStock = inventory.getCurrentStock();
            int newStock = previousStock - itemRequest.quantity();
            inventory.setCurrentStock(newStock);
            inventoryRepository.save(inventory);
            registerSaleMovement(product, user, itemRequest.quantity(), previousStock, newStock);

            subtotal = subtotal.add(itemSubtotal);
        }

        BigDecimal discount = valueOrZero(request.discount()).setScale(2, RoundingMode.HALF_UP);
        if (discount.compareTo(subtotal) > 0) {
            throw new BusinessException("Discount cannot be greater than subtotal");
        }
        BigDecimal taxableAmount = subtotal.subtract(discount);
        BigDecimal tax = taxableAmount
                .multiply(valueOrZero(request.taxPercentage()))
                .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
        BigDecimal total = taxableAmount.add(tax).setScale(2, RoundingMode.HALF_UP);

        sale.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        sale.setDiscount(discount);
        sale.setTax(tax);
        sale.setTotal(total);

        BigDecimal paidAmount = request.paidAmount() == null ? total : request.paidAmount().setScale(2, RoundingMode.HALF_UP);
        if (paidAmount.compareTo(total) > 0) {
            throw new BusinessException("Paid amount cannot be greater than sale total");
        }
        sale.setStatus(paidAmount.compareTo(total) >= 0 ? SaleStatus.PAID : SaleStatus.PENDING);

        Sale savedSale = saleRepository.save(sale);
        Payment payment = new Payment();
        payment.setSale(savedSale);
        payment.setPaymentMethod(request.paymentMethod());
        payment.setAmount(paidAmount);
        payment.setPaidAt(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);

        return saleMapper.toResponse(savedSale, savedPayment, paidAmount);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponse findById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", id));
        java.util.List<Payment> payments = paymentRepository.findBySaleIdOrderByPaidAtAsc(id);
        Payment latestPayment = payments.isEmpty() ? null : payments.get(payments.size() - 1);
        BigDecimal totalPaid = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        return saleMapper.toResponse(sale, latestPayment, totalPaid);
    }

    private void registerSaleMovement(Product product, User user, Integer quantity, Integer previousStock, Integer newStock) {
        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setUser(user);
        movement.setMovementType(StockMovementType.SALE);
        movement.setQuantity(quantity);
        movement.setPreviousStock(previousStock);
        movement.setNewStock(newStock);
        movement.setReason("Sale stock deduction");
        stockMovementRepository.save(movement);
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String generateSaleNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "SALE-" + timestamp + "-" + suffix;
    }
}
