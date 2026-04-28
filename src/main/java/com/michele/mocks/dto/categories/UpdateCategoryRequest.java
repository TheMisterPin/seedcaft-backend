package com.michele.mocks.dto.categories;

public record UpdateCategoryRequest(
        String code,
        String name,
        String description,
        String parentCode) {
}
