package com.michele.mocks.dto.inventory.dashboard;

import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record LineChartDataPointResponse(
        @Size(max = 64) String date,
        BigDecimal value,
        @Size(max = 64) String formattedValue) {
}
