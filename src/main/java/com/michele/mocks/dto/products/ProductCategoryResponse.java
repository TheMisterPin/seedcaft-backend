package com.michele.mocks.dto.products;

public record ProductCategoryResponse(
        Long id,
        String name,
        String description,
        String code,
        String parentCode) {
}