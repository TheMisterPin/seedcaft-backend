package com.michele.mocks.dto.inventory.dashboard;

import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record DashboardDataPointResponse(
        @Size(max = 255) String label,
        BigDecimal value,
        @Size(max = 64) String formattedValue,
        @Size(max = 32) String unit,
        BigDecimal percentage,
        BigDecimal trendValue,
        @Size(max = 16) String trendDirection,
        BigDecimal secondaryValue,
        @Size(max = 64) String secondaryFormattedValue,
        @Size(max = 128) String warehouseCode,
        @Size(max = 255) String warehouseName) {
}
