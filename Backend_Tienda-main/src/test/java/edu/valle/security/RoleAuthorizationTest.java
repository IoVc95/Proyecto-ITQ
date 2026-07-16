package edu.valle.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import edu.valle.modules.catalog.controller.ProductController;
import edu.valle.modules.catalog.service.ProductService;
import edu.valle.modules.cart.controller.CartController;
import edu.valle.modules.cart.service.CartService;
import edu.valle.modules.store.controller.StoreController;
import edu.valle.modules.store.service.StoreService;
import edu.valle.modules.payments.controller.PaymentController;
import edu.valle.modules.payments.service.PaymentService;
import edu.valle.modules.reports.controller.ReportController;
import edu.valle.modules.reports.service.ReportService;
import edu.valle.modules.sales.controller.SaleController;
import edu.valle.modules.sales.service.SaleService;
import edu.valle.modules.users.controller.UserController;
import edu.valle.modules.users.service.UserService;
import edu.valle.modules.supplier.controller.SupplierController;
import edu.valle.modules.supplier.service.SupplierIntegrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(RoleAuthorizationTest.Config.class)
class RoleAuthorizationTest {

    @Autowired
    private UserController userController;

    @Autowired
    private ProductController productController;

    @Autowired
    private SaleController saleController;

    @Autowired
    private ReportController reportController;

    @Autowired
    private PaymentController paymentController;
    @Autowired private CartController cartController;
    @Autowired private StoreController storeController;
    @Autowired private SupplierController supplierController;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanManageUsers() {
        assertDoesNotThrow(() -> userController.findAll());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    void sellerCannotManageUsers() {
        assertThrows(AccessDeniedException.class, () -> userController.findAll());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    void sellerCanManageProducts() {
        assertDoesNotThrow(() -> productController.findAll());
        assertDoesNotThrow(() -> productController.deactivate(1L));
    }

    @Test
    @WithMockUser(roles = "SELLER")
    void sellerCanManageSales() {
        assertDoesNotThrow(() -> saleController.create(null));
        assertDoesNotThrow(() -> saleController.find(1L));
    }

    @Test
    @WithMockUser(roles = "SELLER")
    void sellerCanManagePayments() {
        assertDoesNotThrow(() -> paymentController.addPayment(1L, null));
        assertDoesNotThrow(() -> paymentController.findAll());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerCannotAccessAdministrativeModules() {
        assertThrows(AccessDeniedException.class, () -> productController.findAll());
        assertThrows(AccessDeniedException.class, () -> reportController.getCurrentMonthIncome());
    }

    @Test @WithAnonymousUser
    void publicStoreIsAccessibleWithoutAuthentication() {
        assertDoesNotThrow(() -> storeController.products(null));
    }

    @Test @WithMockUser(username="customer",roles="CUSTOMER")
    void cartAndCheckoutAreCustomerOnly() {
        Authentication authentication=mock(Authentication.class);
        assertDoesNotThrow(() -> cartController.get(authentication));
        assertDoesNotThrow(() -> saleController.checkout(authentication,null));
    }

    @Test @WithMockUser(roles="SELLER")
    void sellerCannotUseCustomerCart() {
        assertThrows(AccessDeniedException.class, () -> cartController.get(mock(Authentication.class)));
    }

    @Test @WithMockUser(roles="ADMIN")
    void adminCanUseSupplierIntegration() {
        assertDoesNotThrow(() -> supplierController.getProduct("SKU-1"));
    }

    @Test @WithMockUser(roles="SELLER")
    void sellerCanUseSupplierIntegration() {
        assertDoesNotThrow(() -> supplierController.getProduct("SKU-1"));
    }

    @Test @WithMockUser(roles="CUSTOMER")
    void customerCannotUseSupplierIntegration() {
        assertThrows(AccessDeniedException.class, () -> supplierController.getProduct("SKU-1"));
    }

    @Configuration
    @EnableMethodSecurity
    static class Config {

        @Bean
        UserService userService() {
            return mock(UserService.class);
        }

        @Bean
        ProductService productService() {
            return mock(ProductService.class);
        }

        @Bean
        SaleService saleService() {
            return mock(SaleService.class);
        }

        @Bean
        ReportService reportService() {
            return mock(ReportService.class);
        }

        @Bean
        PaymentService paymentService() {
            return mock(PaymentService.class);
        }
        @Bean CartService cartService(){return mock(CartService.class);}
        @Bean StoreService storeService(){return mock(StoreService.class);}
        @Bean SupplierIntegrationService supplierIntegrationService(){return mock(SupplierIntegrationService.class);}

        @Bean
        UserController userController(UserService userService) {
            return new UserController(userService);
        }

        @Bean
        ProductController productController(ProductService productService) {
            return new ProductController(productService);
        }

        @Bean
        SaleController saleController(SaleService saleService) {
            return new SaleController(saleService);
        }

        @Bean
        ReportController reportController(ReportService reportService) {
            return new ReportController(reportService);
        }

        @Bean
        PaymentController paymentController(PaymentService paymentService) {
            return new PaymentController(paymentService);
        }
        @Bean CartController cartController(CartService cartService){return new CartController(cartService);}
        @Bean StoreController storeController(StoreService storeService){return new StoreController(storeService);}
        @Bean SupplierController supplierController(SupplierIntegrationService service){return new SupplierController(service);}
    }
}
