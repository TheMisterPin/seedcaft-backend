package com.michele.mocks.specification;

import com.michele.mocks.entity.Category;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class CategorySpecifications {

    private CategorySpecifications() {
    }

    public static Specification<Category> textSearch(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) {
                return cb.conjunction();
            }

            String likeValue = "%" + q.toLowerCase(Locale.ROOT).trim() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("code")), likeValue),
                    cb.like(cb.lower(root.get("name")), likeValue),
                    cb.like(cb.lower(root.get("description")), likeValue));
        };
    }

    public static Specification<Category> hasParentId(Long parentId) {
        return (root, query, cb) -> {
            if (parentId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.join("parent").get("id"), parentId);
        };
    }

    public static Specification<Category> hasParentCode(String parentCode) {
        return (root, query, cb) -> {
            if (parentCode == null || parentCode.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.join("parent").get("code")), parentCode.toLowerCase(Locale.ROOT).trim());
        };
    }
}
