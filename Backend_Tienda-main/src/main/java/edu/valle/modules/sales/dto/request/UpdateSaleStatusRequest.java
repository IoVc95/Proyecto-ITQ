package edu.valle.modules.sales.dto.request;
import edu.valle.common.enums.SaleStatus; import jakarta.validation.constraints.NotNull;
public record UpdateSaleStatusRequest(@NotNull SaleStatus status) {}
