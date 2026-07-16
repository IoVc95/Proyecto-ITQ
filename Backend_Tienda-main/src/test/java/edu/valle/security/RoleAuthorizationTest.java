package edu.valle.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import edu.valle.modules.catalog.controller.ProductController;
import edu.valle.modules.catalog.service.ProductService;
import edu.valle.modules.payments.controller.PaymentController;
import edu.valle.modules.payments.service.PaymentService;
import edu.valle.modules.reports.controller.ReportController;
import edu.valle.modules.reports.service.ReportService;
import edu.valle.modules.sales.controller.SaleController;
import edu.valle.modules.sales.service.SaleService;
import edu.valle.modules.users.controller.UserController;
import edu.valle.modules.users.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
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

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanManageUsers() {
        assertDoesNotThrow(() -> userController.findAll());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void managerCannotManageUsers() {
        assertThrows(AccessDeniedException.class, () -> userController.findAll());
    }

    @Test
    @WithMockUser(roles = "CASHIER")
    void cashierCanConsultProductsButCannotManageThem() {
        assertDoesNotThrow(() -> productController.findAll());
        assertThrows(AccessDeniedException.class, () -> productController.deactivate(1L));
    }

    @Test
    @WithMockUser(roles = "CASHIER")
    void cashierCanCreateSalesButCannotConsultSales() {
        assertDoesNotThrow(() -> saleController.create(null));
        assertThrows(AccessDeniedException.class, () -> saleController.findById(1L));
    }

    @Test
    @WithMockUser(roles = "CASHIER")
    void cashierCanAddPaymentsButCannotListThem() {
        assertDoesNotThrow(() -> paymentController.addPayment(1L, null));
        assertThrows(AccessDeniedException.class, () -> paymentController.findAll());
    }

    @Test
    @WithMockUser(roles = "USER")
    void normalUserCannotAccessInternalModules() {
        assertThrows(AccessDeniedException.class, () -> productController.findAll());
        assertThrows(AccessDeniedException.class, () -> reportController.getCurrentMonthIncome());
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
    }
}
