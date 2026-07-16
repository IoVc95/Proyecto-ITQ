package edu.valle.modules.reports.controller;

import edu.valle.modules.reports.dto.response.BestSellingProductResponse;
import edu.valle.modules.reports.dto.response.CategorySalesResponse;
import edu.valle.modules.reports.dto.response.DailyIncomeResponse;
import edu.valle.modules.reports.dto.response.IncomeRangeResponse;
import edu.valle.modules.reports.dto.response.LowStockReportResponse;
import edu.valle.modules.reports.dto.response.MonthlyIncomeResponse;
import edu.valle.modules.reports.dto.response.MonthlySalesResponse;
import edu.valle.modules.reports.dto.response.MonthlySalesTotalResponse;
import edu.valle.modules.reports.dto.response.PaymentMethodSummaryResponse;
import edu.valle.modules.reports.dto.response.TopProductResponse;
import edu.valle.modules.reports.dto.response.UserSalesResponse;
import edu.valle.modules.reports.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
@Tag(name = "Reports", description = "Reportes de ventas e ingresos")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/monthly-income")
    @Operation(summary = "Consultar ingresos del mes", description = "Obtiene el total de ingresos del mes actual.")
    @ApiResponse(responseCode = "200", description = "Ingresos mensuales obtenidos correctamente")
    public MonthlyIncomeResponse getCurrentMonthIncome() {
        return reportService.getCurrentMonthIncome();
    }

    @GetMapping("/monthly-sales-total")
    @Operation(summary = "Consultar total de ventas del mes", description = "Obtiene el total de ventas realizadas en el mes actual.")
    @ApiResponse(responseCode = "200", description = "Total mensual de ventas obtenido correctamente")
    public MonthlySalesTotalResponse getCurrentMonthSalesTotal() {
        return reportService.getCurrentMonthSalesTotal();
    }

    @GetMapping("/top-products")
    @Operation(summary = "Consultar productos más vendidos", description = "Obtiene los productos más vendidos del mes actual.")
    @ApiResponse(responseCode = "200", description = "Productos más vendidos obtenidos correctamente")
    public List<TopProductResponse> getTopProductsCurrentMonth() {
        return reportService.getTopProductsCurrentMonth();
    }

    @GetMapping("/income")
    @Operation(summary = "Consultar ingresos por rango", description = "Suma pagos reales dentro del rango indicado.")
    public IncomeRangeResponse getIncomeByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return reportService.getIncomeByRange(from, to);
    }

    @GetMapping("/income/daily")
    @Operation(summary = "Consultar ingresos diarios", description = "Agrupa pagos reales por dÃ­a.")
    public List<DailyIncomeResponse> getDailyIncome(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return reportService.getDailyIncome(from, to);
    }

    @GetMapping("/sales/monthly")
    @Operation(summary = "Consultar ventas por mes", description = "Agrupa ventas no anuladas por mes.")
    public List<MonthlySalesResponse> getMonthlySales(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth from,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth to
    ) {
        return reportService.getMonthlySales(from, to);
    }

    @GetMapping("/products/top")
    @Operation(summary = "Consultar productos mÃ¡s vendidos por rango")
    public List<BestSellingProductResponse> getTopProducts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return reportService.getTopProducts(from, to, limit);
    }

    @GetMapping("/inventory/low-stock")
    @Operation(summary = "Consultar productos con stock bajo")
    public List<LowStockReportResponse> getLowStockProducts() {
        return reportService.getLowStockProducts();
    }

    @GetMapping("/sales/by-user")
    @Operation(summary = "Consultar ventas por usuario")
    public List<UserSalesResponse> getSalesByUser(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return reportService.getSalesByUser(from, to);
    }

    @GetMapping("/payments/by-method")
    @Operation(summary = "Consultar pagos por mÃ©todo")
    public List<PaymentMethodSummaryResponse> getPaymentsByMethod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return reportService.getPaymentsByMethod(from, to);
    }

    @GetMapping("/sales/by-category")
    @Operation(summary = "Consultar ventas por categorÃ­a")
    public List<CategorySalesResponse> getSalesByCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return reportService.getSalesByCategory(from, to);
    }
}
