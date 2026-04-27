package com.michele.mocks.dto.products;

public record ProductResponse(
                Long id,
                String sku,
                String name,
                String description,
                String barcode,
                String categoryId,
                String imageUrl,
                Double sellPrice,
                Double purchasePrice,
                String currency) {
}