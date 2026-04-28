package com.michele.mocks.dto.categories;

import java.math.BigDecimal;

public record CategoryProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal sellPrice,
        BigDecimal purchasePrice,
        String currency) {
}
