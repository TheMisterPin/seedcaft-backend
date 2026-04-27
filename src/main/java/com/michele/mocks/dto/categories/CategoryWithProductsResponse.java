package com.michele.mocks.dto.categories;

import java.util.List;

public record CategoryWithProductsResponse(
        Long id,
        String code,
        String name,
        String description,
        List<CategoryProductResponse> products) {
}