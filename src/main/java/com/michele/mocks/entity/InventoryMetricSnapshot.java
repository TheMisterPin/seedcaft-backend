package com.michele.mocks.entity;

import com.michele.mocks.entity.enums.MetricScopeType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
public class InventoryMetricSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate snapshotDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private MetricScopeType scopeType;

    @NotBlank
    @Size(max = 64)
    @Column(nullable = false, length = 64)
    private String scopeCode;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String scopeName;

    @PositiveOrZero
    @Column(nullable = false)
    private Long totalUnits;

    @PositiveOrZero
    @Column(nullable = false)
    private Long availableUnits;

    @PositiveOrZero
    @Column(nullable = false)
    private Long reservedUnits;

    @PositiveOrZero
    @Column(nullable = false)
    private Long blockedUnits;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer lowStockCount;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer outOfStockCount;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer totalStorageCapacity;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer usedStorageUnits;

    @PositiveOrZero
    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal fillPercentage;

    @PositiveOrZero
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal inventoryValue;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
