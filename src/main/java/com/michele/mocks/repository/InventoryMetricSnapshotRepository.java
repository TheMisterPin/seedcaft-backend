package com.michele.mocks.repository;

import com.michele.mocks.entity.InventoryMetricSnapshot;
import com.michele.mocks.entity.enums.MetricScopeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InventoryMetricSnapshotRepository extends JpaRepository<InventoryMetricSnapshot, Long> {

    Optional<InventoryMetricSnapshot> findTopByScopeTypeOrderBySnapshotDateDescCreatedAtDesc(MetricScopeType scopeType);

    Optional<InventoryMetricSnapshot> findTopByScopeTypeAndScopeCodeIgnoreCaseOrderBySnapshotDateDescCreatedAtDesc(
            MetricScopeType scopeType,
            String scopeCode
    );

    List<InventoryMetricSnapshot> findByScopeTypeOrderBySnapshotDateDescCreatedAtDesc(
            MetricScopeType scopeType,
            Pageable pageable
    );

    List<InventoryMetricSnapshot> findByScopeTypeAndSnapshotDateBetweenOrderBySnapshotDateDescCreatedAtDesc(
            MetricScopeType scopeType,
            LocalDate startDate,
            LocalDate endDate
    );

    List<InventoryMetricSnapshot> findByScopeTypeAndScopeCodeIgnoreCaseAndSnapshotDateBetweenOrderBySnapshotDateDescCreatedAtDesc(
            MetricScopeType scopeType,
            String scopeCode,
            LocalDate startDate,
            LocalDate endDate
    );
}
