package com.michele.mocks.specification;

import com.michele.mocks.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Locale;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> textSearch(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) {
                return cb.conjunction();
            }

            String likeValue = "%" + q.toLowerCase(Locale.ROOT).trim() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), likeValue),
                    cb.like(cb.lower(root.get("description")), likeValue),
                    cb.like(cb.lower(root.get("sku")), likeValue),
                    cb.like(cb.lower(root.get("barcode")), likeValue));
        };
    }

    public static Specification<Product> hasCategoryId(Long categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.join("category").get("id"), categoryId);
        };
    }

    public static Specification<Product> hasCategoryCode(String categoryCode) {
        return (root, query, cb) -> {
            if (categoryCode == null || categoryCode.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.join("category").get("code")), categoryCode.toLowerCase(Locale.ROOT).trim());
        };
    }

    public static Specification<Product> minPrice(BigDecimal minPrice) {
        return (root, query, cb) -> {
            if (minPrice == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("sellPrice"), minPrice);
        };
    }

    public static Specification<Product> maxPrice(BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("sellPrice"), maxPrice);
        };
    }

    public static Specification<Product> hasCurrency(String currency) {
        return (root, query, cb) -> {
            if (currency == null || currency.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("currency")), currency.toLowerCase(Locale.ROOT).trim());
        };
    }
}
