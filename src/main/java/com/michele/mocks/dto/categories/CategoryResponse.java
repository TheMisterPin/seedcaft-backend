package com.michele.mocks.dto.categories;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String code,
        String name,
        String description,
        Long parentId,
        String parentCode,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
