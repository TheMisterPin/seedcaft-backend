package com.michele.mocks.dto.categories;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
        @NotBlank @Size(max = 64) String code,
        @NotBlank @Size(max = 255) String name,
        @Size(max = 1024) String description,
        Long parentId,
        @Size(max = 64) String parentCode) {
}
