package com.michele.mocks.dto.categories;

public record CategoryProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        Double sellPrice,
        Double purchasePrice,
        String currency) {
}