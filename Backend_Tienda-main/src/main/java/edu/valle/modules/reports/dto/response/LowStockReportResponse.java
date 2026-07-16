package edu.valle.modules.reports.dto.response;

public record LowStockReportResponse(
        Long productId,
        String productName,
        String sku,
        Integer currentStock,
        Integer minStock
) {
}
