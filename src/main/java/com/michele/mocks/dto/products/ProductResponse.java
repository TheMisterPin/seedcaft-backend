package com.michele.mocks.dto.products;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        String barcode,
        Long categoryId,
        String trackingMode,
        String uom,
        Double weightKg,
        Double lengthCm,
        Double widthCm,
        Double heightCm,
        Integer minQuantity,
        String imageUrl,
        BigDecimal sellPrice,
        BigDecimal purchasePrice,
        String currency,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
