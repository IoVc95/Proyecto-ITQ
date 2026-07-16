package edu.valle.modules.reports.service;

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
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

public interface ReportService {

    MonthlyIncomeResponse getCurrentMonthIncome();

    MonthlySalesTotalResponse getCurrentMonthSalesTotal();

    List<TopProductResponse> getTopProductsCurrentMonth();

    IncomeRangeResponse getIncomeByRange(LocalDateTime from, LocalDateTime to);

    List<DailyIncomeResponse> getDailyIncome(LocalDateTime from, LocalDateTime to);

    List<MonthlySalesResponse> getMonthlySales(YearMonth from, YearMonth to);

    List<BestSellingProductResponse> getTopProducts(LocalDateTime from, LocalDateTime to, int limit);

    List<LowStockReportResponse> getLowStockProducts();

    List<UserSalesResponse> getSalesByUser(LocalDateTime from, LocalDateTime to);

    List<PaymentMethodSummaryResponse> getPaymentsByMethod(LocalDateTime from, LocalDateTime to);

    List<CategorySalesResponse> getSalesByCategory(LocalDateTime from, LocalDateTime to);
}
