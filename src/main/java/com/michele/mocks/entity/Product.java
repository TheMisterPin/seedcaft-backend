package com.michele.mocks.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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

    @NotBlank
    @Size(max = 64)
    private String sku;
    @NotBlank
    @Size(max = 255)
    private String name;
    @Size(max = 1024)
    private String description;
    @Size(max = 128)
    private String barcode;

    private String trackingMode;
    private String uom;

    @PositiveOrZero
    private double weightKg;

    @PositiveOrZero
    private double lengthCm;
    @PositiveOrZero
    private double widthCm;
    @PositiveOrZero
    private double heightCm;

    @PositiveOrZero
    private int minQuantity;

    private String imageUrl;
    @ElementCollection
    @CollectionTable(name = "product_image_urls", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    @PositiveOrZero
    private BigDecimal sellPrice;
    @PositiveOrZero
    private BigDecimal purchPrice;

    @NotBlank
    @Size(min = 3, max = 3)
    private String currency;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
