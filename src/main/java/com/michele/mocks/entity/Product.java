package com.michele.mocks.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sku;
    private String name;
    private String description;
    private String barcode;

    /**
     * Not a separate column: {@code category_id} is mapped by {@link #category} only.
     * Used for JSON request binding (e.g. "categoryId": "1") when {@link #category} is absent.
     */
    @Transient
    private String categoryId;
    private String trackingMode;
    private String uom;

    private double weightKg;

    private double lengthCm;
    private double widthCm;
    private double heightCm;

    private int minQuantity;

    private String imageUrl;
    private List<String> imageUrls;

    private BigDecimal sellPrice;
    private BigDecimal purchPrice;

    private String currency;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}