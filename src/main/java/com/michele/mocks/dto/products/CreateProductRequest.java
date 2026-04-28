package com.michele.mocks.dto.products;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductRequest(
        @NotBlank @Size(max = 64) String sku,
        @NotBlank @Size(max = 255) String name,
        @Size(max = 2048) String description,
        @Size(max = 128) String barcode,
        Long categoryId,
        @Size(max = 32) String trackingMode,
        @Size(max = 32) String uom,
        @PositiveOrZero Double weightKg,
        @PositiveOrZero Double lengthCm,
        @PositiveOrZero Double widthCm,
        @PositiveOrZero Double heightCm,
        @PositiveOrZero Integer minQuantity,
        @Size(max = 2048) String imageUrl,
        List<@Size(max = 2048) String> imageUrls,
        @PositiveOrZero BigDecimal sellPrice,
        @PositiveOrZero BigDecimal purchasePrice,
        @NotBlank @Size(min = 3, max = 3) String currency) {
}
