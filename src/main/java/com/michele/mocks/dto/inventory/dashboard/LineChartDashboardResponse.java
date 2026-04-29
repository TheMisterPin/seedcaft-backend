package com.michele.mocks.dto.inventory.dashboard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record LineChartDashboardResponse(
        @NotNull @Valid DashboardMetaResponse meta,
        @NotBlank @Size(max = 128) String xKey,
        @NotBlank @Size(max = 128) String yKey,
        @NotEmpty List<@Valid LineChartSeriesResponse> series) {
}
