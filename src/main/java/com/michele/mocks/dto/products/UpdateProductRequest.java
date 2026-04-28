package com.michele.mocks.dto.products;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProductRequest(
        @NotBlank @Size(max = 64) String sku,
        @NotBlank @Size(max = 255) String name,
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
        Double sellPrice,
        Double purchasePrice,
        @NotBlank @Size(min = 3, max = 3) String currency) {
}
