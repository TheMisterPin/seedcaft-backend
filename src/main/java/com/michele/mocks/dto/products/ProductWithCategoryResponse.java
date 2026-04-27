package com.michele.mocks.dto.products;

public record ProductWithCategoryResponse(
        Long id,
        String sku,
        String name,
        String description,
        ProductCategoryResponse category) {
}