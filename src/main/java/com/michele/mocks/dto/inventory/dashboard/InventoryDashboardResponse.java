package com.michele.mocks.dto.inventory.dashboard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

public record InventoryDashboardResponse(
        @NotEmpty Map<String, @Valid Object> sections) {
}
