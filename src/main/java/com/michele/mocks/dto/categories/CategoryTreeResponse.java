package com.michele.mocks.dto.categories;

import java.util.List;

public record CategoryTreeResponse(
        Long id,
        String code,
        String name,
        String description,
        List<CategoryTreeResponse> children) {
}