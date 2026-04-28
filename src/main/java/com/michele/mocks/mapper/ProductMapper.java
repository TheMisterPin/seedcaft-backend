package com.michele.mocks.mapper;

import com.michele.mocks.dto.categories.CategoryProductResponse;
import com.michele.mocks.dto.products.ProductCategoryResponse;
import com.michele.mocks.dto.products.ProductResponse;
import com.michele.mocks.dto.products.ProductWithCategoryResponse;
import com.michele.mocks.entity.Category;
import com.michele.mocks.entity.Product;

public final class ProductMapper {

    private ProductMapper() {
    }

    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getBarcode(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getTrackingMode(),
                product.getUom(),
                product.getWeightKg(),
                product.getLengthCm(),
                product.getWidthCm(),
                product.getHeightCm(),
                product.getMinQuantity(),
                product.getImageUrl(),
                product.getSellPrice(),
                product.getPurchPrice(),
                product.getCurrency(),
                product.getCreatedAt(),
                product.getUpdatedAt());
    }

    public static ProductWithCategoryResponse toWithCategoryResponse(Product product) {
        return new ProductWithCategoryResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                toCategoryResponse(product.getCategory()));
    }

    public static CategoryProductResponse toCategoryProductResponse(Product product) {
        return new CategoryProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getSellPrice(),
                product.getPurchPrice(),
                product.getCurrency());
    }

    private static ProductCategoryResponse toCategoryResponse(Category category) {
        if (category == null) {
            return null;
        }
        return new ProductCategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCode(),
                category.getParentCode());
    }
}
