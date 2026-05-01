package com.michele.mocks.dto.inventory.dashboard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record LineChartSeriesResponse(
        @NotBlank @Size(max = 128) String key,
        @NotBlank @Size(max = 128) String label,
        @NotBlank @Size(max = 32) String unit,
        List<@Valid LineChartDataPointResponse> data) {
}
