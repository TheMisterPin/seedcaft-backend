package com.michele.mocks.service;

import com.michele.mocks.entity.InventoryMetricSnapshot;
import com.michele.mocks.entity.enums.MetricScopeType;
import com.michele.mocks.exception.BadRequestException;
import com.michele.mocks.repository.InventoryMetricSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.michele.mocks.util.MetricRange;

@Service
@RequiredArgsConstructor
public class InventoryMetricService {

    private static final int DEFAULT_TREND_OFFSET_DAYS = 7;

    private final InventoryMetricSnapshotRepository snapshotRepository;

    public Optional<InventoryMetricSnapshot> findLatestSnapshot(MetricScopeType scopeType, String scopeCode) {
        if (scopeType == null) {
            throw new BadRequestException("scopeType is required");
        }

        if (scopeCode == null || scopeCode.isBlank()) {
            return snapshotRepository.findTopByScopeTypeOrderBySnapshotDateDescCreatedAtDesc(scopeType);
        }

        return snapshotRepository.findTopByScopeTypeAndScopeCodeIgnoreCaseOrderBySnapshotDateDescCreatedAtDesc(
                scopeType,
                scopeCode.trim()
        );
    }

    public int parseRangeDays(String range) {
        return MetricRange.parse(range).days();
    }

    public List<InventoryMetricSnapshot> findSnapshotsInRange(
            MetricScopeType scopeType,
            String scopeCode,
            String range
    ) {
        int rangeDays = parseRangeDays(range);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(rangeDays - 1L);

        if (scopeCode == null || scopeCode.isBlank()) {
            return snapshotRepository.findByScopeTypeAndSnapshotDateBetweenOrderBySnapshotDateDescCreatedAtDesc(
                    scopeType,
                    startDate,
                    endDate
            );
        }

        return snapshotRepository.findByScopeTypeAndScopeCodeIgnoreCaseAndSnapshotDateBetweenOrderBySnapshotDateDescCreatedAtDesc(
                scopeType,
                scopeCode.trim(),
                startDate,
                endDate
        );
    }

    public BigDecimal calculateTrendPercentage(List<InventoryMetricSnapshot> rangeSnapshots, Long latestValue) {
        if (latestValue == null) {
            return BigDecimal.ZERO;
        }
        return calculateTrendPercentage(rangeSnapshots, BigDecimal.valueOf(latestValue), snapshot ->
                snapshot.getTotalUnits() == null ? null : BigDecimal.valueOf(snapshot.getTotalUnits()));
    }

    public BigDecimal calculateTrendPercentage(
            List<InventoryMetricSnapshot> rangeSnapshots,
            BigDecimal latestValue,
            Function<InventoryMetricSnapshot, BigDecimal> baselineExtractor
    ) {
        if (latestValue == null || baselineExtractor == null || rangeSnapshots == null || rangeSnapshots.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal baseline = rangeSnapshots.stream()
                .min(Comparator.comparing(InventoryMetricSnapshot::getSnapshotDate))
                .map(baselineExtractor)
                .orElse(null);

        if (baseline == null || BigDecimal.ZERO.compareTo(baseline) == 0) {
            return BigDecimal.ZERO;
        }

        return latestValue.subtract(baseline)
                .multiply(BigDecimal.valueOf(100))
                .divide(baseline, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTrendPercentage(
            MetricScopeType scopeType,
            String scopeCode,
            Long latestValue,
            int fixedOffsetDays
    ) {
        if (latestValue == null) {
            return BigDecimal.ZERO;
        }

        int offset = fixedOffsetDays <= 0 ? DEFAULT_TREND_OFFSET_DAYS : fixedOffsetDays;
        LocalDate latestDate = LocalDate.now();
        LocalDate startDate = latestDate.minusDays(offset);

        List<InventoryMetricSnapshot> snapshots;
        if (scopeCode == null || scopeCode.isBlank()) {
            snapshots = snapshotRepository.findByScopeTypeAndSnapshotDateBetweenOrderBySnapshotDateDescCreatedAtDesc(
                    scopeType,
                    startDate,
                    latestDate
            );
        } else {
            snapshots = snapshotRepository.findByScopeTypeAndScopeCodeIgnoreCaseAndSnapshotDateBetweenOrderBySnapshotDateDescCreatedAtDesc(
                    scopeType,
                    scopeCode.trim(),
                    startDate,
                    latestDate
            );
        }

        Long baseline = snapshots.stream()
                .min(Comparator.comparing(InventoryMetricSnapshot::getSnapshotDate))
                .map(InventoryMetricSnapshot::getTotalUnits)
                .orElse(null);

        if (baseline == null || baseline == 0L) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(latestValue - baseline)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(baseline), 2, RoundingMode.HALF_UP);
    }

    public Optional<InventoryMetricSnapshot> findLatestGlobalSnapshot() {
        return snapshotRepository.findByScopeTypeOrderBySnapshotDateDescCreatedAtDesc(
                MetricScopeType.GLOBAL,
                PageRequest.of(0, 1)
        ).stream().findFirst();
    }
}
