package com.michele.mocks.mapper;

import com.michele.mocks.dto.categories.CategoryResponse;
import com.michele.mocks.dto.categories.CategoryTreeResponse;
import com.michele.mocks.entity.Category;

import java.util.List;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getCode(),
                category.getName(),
                category.getDescription(),
                category.getParent() != null ? category.getParent().getId() : null,
                category.getParentCode(),
                category.getCreatedAt(),
                category.getUpdatedAt());
    }

    public static CategoryTreeResponse toTreeResponse(Category category) {
        List<CategoryTreeResponse> children = category.getChildren()
                .stream()
                .map(CategoryMapper::toTreeResponse)
                .toList();

        return new CategoryTreeResponse(
                category.getId(),
                category.getCode(),
                category.getName(),
                category.getDescription(),
                children);
    }
}
