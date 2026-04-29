package com.michele.mocks.dto.inventory.dashboard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record LineChartSeriesResponse(
        @NotBlank @Size(max = 128) String name,
        List<@Valid DashboardDataPointResponse> data) {
}
