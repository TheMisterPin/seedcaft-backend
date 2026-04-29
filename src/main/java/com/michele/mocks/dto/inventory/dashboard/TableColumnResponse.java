package com.michele.mocks.dto.inventory.dashboard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TableColumnResponse(
        @NotBlank @Size(max = 128) String key,
        @NotBlank @Size(max = 255) String label) {
}
