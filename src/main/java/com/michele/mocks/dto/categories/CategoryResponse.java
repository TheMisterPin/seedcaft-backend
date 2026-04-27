package com.michele.mocks.dto.categories;

public record CategoryResponse(
        Long id,
        String code,
        String name,
        String description) {
}