package com.michele.mocks.dto.inventory.dashboard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LowStockRowResponse(
        @NotBlank String sku,
        @NotBlank String productName,
        @NotBlank String categoryName,
        @NotBlank String warehouseCode,
        @NotNull Integer availableUnits,
        @NotNull Integer reorderPoint,
        @NotBlank String status) {
}
