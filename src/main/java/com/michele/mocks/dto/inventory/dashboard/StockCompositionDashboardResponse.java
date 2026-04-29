package com.michele.mocks.dto.inventory.dashboard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record StockCompositionDashboardResponse(
        @NotNull @Valid DashboardMetaResponse meta,
        @NotNull @Valid TotalResponse total,
        List<@Valid DashboardDataPointResponse> segments) {

    public record TotalResponse(
            @NotNull BigDecimal value,
            @NotNull String formattedValue) {
    }
}
