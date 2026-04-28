package com.michele.mocks.dto.categories;

public record CreateCategoryRequest(
        String code,
        String name,
        String description,
        String parentCode) {
}
