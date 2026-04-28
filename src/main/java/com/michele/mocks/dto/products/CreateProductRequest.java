package com.michele.mocks.dto.products;

public record CreateProductRequest(
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
        Double sellPrice,
        Double purchasePrice,
        String currency) {
}
