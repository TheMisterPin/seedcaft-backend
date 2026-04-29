package com.michele.mocks.dto.inventory.dashboard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record DashboardMetaResponse(
        @NotBlank @Size(max = 255) String title,
        @NotNull DashboardType type,
        @NotNull Instant generatedAt,
        @Size(max = 512) String description,
        @Size(max = 64) String scope,
        @Size(max = 32) String range,
        @Size(max = 128) String warehouseCode,
        @Size(max = 128) String categoryCode,
        Integer limit) {
}
