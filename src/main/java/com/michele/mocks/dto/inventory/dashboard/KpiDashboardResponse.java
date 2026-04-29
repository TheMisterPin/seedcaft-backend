package com.michele.mocks.dto.inventory.dashboard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record KpiDashboardResponse(
        @NotNull @Valid DashboardMetaResponse meta,
        @NotEmpty List<@Valid DashboardDataPointResponse> data) {
}
