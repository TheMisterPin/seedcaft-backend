package com.michele.mocks.dto.products;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        String barcode,
        Long categoryId,
        String categoryCode,
        String categoryName,
        String trackingMode,
        String uom,
        Double weightKg,
        Double lengthCm,
        Double widthCm,
        Double heightCm,
        Integer minQuantity,
        String imageUrl,
        List<String> imageUrls,
        BigDecimal sellPrice,
        BigDecimal purchasePrice,
        String currency,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
