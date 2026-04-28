package com.michele.mocks.entity;

import com.michele.mocks.entity.enums.StockStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class InventoryStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bin_id", nullable = false)
    private StorageBin bin;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer quantityOnHand;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer quantityAvailable;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer quantityReserved;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer quantityBlocked;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer reorderPoint;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private StockStatus stockStatus;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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
