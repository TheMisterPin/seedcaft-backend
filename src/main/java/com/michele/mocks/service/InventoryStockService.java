package com.michele.mocks.service;

import com.michele.mocks.dto.inventory.dashboard.DashboardDataPointResponse;
import com.michele.mocks.dto.inventory.dashboard.LowStockRowResponse;
import com.michele.mocks.entity.InventoryStock;
import com.michele.mocks.entity.StorageBin;
import com.michele.mocks.entity.Warehouse;
import com.michele.mocks.exception.ResourceNotFoundException;
import com.michele.mocks.repository.InventoryStockRepository;
import com.michele.mocks.repository.StorageBinRepository;
import com.michele.mocks.repository.WarehouseRepository;
import com.michele.mocks.repository.CategoryRepository;
import com.michele.mocks.util.DashboardFormatters;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InventoryStockService {

    private final InventoryStockRepository inventoryStockRepository;
    private final StorageBinRepository storageBinRepository;
    private final WarehouseRepository warehouseRepository;
    private final CategoryRepository categoryRepository;

    public List<LowStockRowResponse> getLowStockTableRows(int limit, String warehouseCode, String categoryCode) {
        int size = limit > 0 ? limit : 10;
        if (isUnknownWarehouse(warehouseCode) || isUnknownCategory(categoryCode)) {
            return List.of();
        }
        return inventoryStockRepository.findLowStockByUrgencyFiltered(normalizeCode(warehouseCode), normalizeCode(categoryCode), PageRequest.of(0, size))
                .stream()
                .map(this::toLowStockRow)
                .toList();
    }

    public List<DashboardDataPointResponse> getTopBinsData(int limit, String warehouseCode, String categoryCode) {
        int size = limit > 0 ? limit : 10;
        if (isUnknownWarehouse(warehouseCode) || isUnknownCategory(categoryCode)) {
            return List.of();
        }
        return storageBinRepository.findTopByUtilizationFiltered(normalizeCode(warehouseCode), normalizeCode(categoryCode), PageRequest.of(0, size))
                .stream()
                .map(this::toTopBinDataPoint)
                .toList();
    }

    public List<DashboardDataPointResponse> getBinHeatmapData(String warehouseCode, String categoryCode) {
        if (isUnknownWarehouse(warehouseCode) || isUnknownCategory(categoryCode)) {
            return List.of();
        }
        List<StorageBin> bins = normalizeCode(warehouseCode) == null
                ? storageBinRepository.findAll()
                : storageBinRepository.findByWarehouseCodeIgnoreCase(normalizeCode(warehouseCode));

        if (normalizeCode(categoryCode) != null) {
            bins = bins.stream()
                    .filter(bin -> bin.getStocks() != null && bin.getStocks().stream().anyMatch(stock -> stock.getProduct().getCategory() != null && categoryCode.equalsIgnoreCase(stock.getProduct().getCategory().getCode())))
                    .toList();
        }

        return bins.stream()
                .map(this::toHeatmapDataPoint)
                .toList();
    }

    public List<DashboardDataPointResponse> getStockCompositionData(String warehouseCode, String categoryCode) {
        if (isUnknownWarehouse(warehouseCode) || isUnknownCategory(categoryCode)) {
            return List.of(toSegment("Available", 0, 0), toSegment("Reserved", 0, 0), toSegment("Blocked", 0, 0));
        }
        List<InventoryStock> stockList = inventoryStockRepository.findAll().stream()
                .filter(stock -> normalizeCode(warehouseCode) == null || stock.getWarehouse().getCode().equalsIgnoreCase(warehouseCode.trim()))
                .filter(stock -> normalizeCode(categoryCode) == null || (stock.getProduct().getCategory() != null && stock.getProduct().getCategory().getCode().equalsIgnoreCase(categoryCode.trim())))
                .toList();

        long available = sumSafe(stockList, InventoryStock::getQuantityAvailable);
        long reserved = sumSafe(stockList, InventoryStock::getQuantityReserved);
        long blocked = sumSafe(stockList, InventoryStock::getQuantityBlocked);
        long total = available + reserved + blocked;

        List<DashboardDataPointResponse> segments = new ArrayList<>();
        segments.add(toSegment("Available", available, total));
        segments.add(toSegment("Reserved", reserved, total));
        segments.add(toSegment("Blocked", blocked, total));
        return segments;
    }

    public Warehouse requireWarehouse(String warehouseCode) {
        return warehouseRepository.findByCodeIgnoreCase(warehouseCode)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found: " + warehouseCode));
    }

    private LowStockRowResponse toLowStockRow(InventoryStock stock) {
        String categoryName = stock.getProduct().getCategory() == null
                ? "Uncategorized"
                : stock.getProduct().getCategory().getName();
        return new LowStockRowResponse(
                stock.getProduct().getSku(),
                stock.getProduct().getName(),
                categoryName,
                stock.getWarehouse().getCode(),
                stock.getQuantityAvailable(),
                stock.getReorderPoint(),
                stock.getStockStatus().name()
        );
    }

    private DashboardDataPointResponse toTopBinDataPoint(StorageBin bin) {
        BigDecimal utilization = BigDecimal.ZERO;
        if (bin.getMaxStorageUnits() != null && bin.getMaxStorageUnits() > 0) {
            utilization = BigDecimal.valueOf(bin.getCurrentStorageUnits())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(bin.getMaxStorageUnits()), 2, RoundingMode.HALF_UP);
        }

        String label = bin.getWarehouse().getCode() + " - " + bin.getCode();
        return new DashboardDataPointResponse(
                label,
                utilization,
                DashboardFormatters.formatPercentage(utilization),
                "%",
                null,
                null,
                null,
                null,
                BigDecimal.valueOf(Objects.requireNonNullElse(bin.getCurrentStorageUnits(), 0)),
                DashboardFormatters.formatInteger(Objects.requireNonNullElse(bin.getCurrentStorageUnits(), 0))
                        + " / " + DashboardFormatters.formatInteger(Objects.requireNonNullElse(bin.getMaxStorageUnits(), 0))
                        + " units",
                bin.getWarehouse().getCode(),
                bin.getWarehouse().getName()
        );
    }

    private DashboardDataPointResponse toHeatmapDataPoint(StorageBin bin) {
        BigDecimal utilization = BigDecimal.ZERO;
        if (bin.getMaxStorageUnits() != null && bin.getMaxStorageUnits() > 0) {
            utilization = BigDecimal.valueOf(bin.getCurrentStorageUnits())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(bin.getMaxStorageUnits()), 2, RoundingMode.HALF_UP);
        }

        String label = bin.getWarehouse().getCode() + ":" + bin.getCode();
        return new DashboardDataPointResponse(
                label,
                utilization,
                DashboardFormatters.formatPercentage(utilization),
                "%",
                toHeatmapStatus(utilization),
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private DashboardDataPointResponse toSegment(String label, long value, long total) {
        BigDecimal percentage = total <= 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(value)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
        return new DashboardDataPointResponse(
                label,
                BigDecimal.valueOf(value),
                DashboardFormatters.formatInteger(value) + " units",
                "units",
                null,
                percentage,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private String toHeatmapStatus(BigDecimal utilization) {
        if (utilization.compareTo(BigDecimal.ZERO) == 0) {
            return "empty";
        }
        if (utilization.compareTo(BigDecimal.valueOf(30)) <= 0) {
            return "low";
        }
        if (utilization.compareTo(BigDecimal.valueOf(75)) <= 0) {
            return "normal";
        }
        if (utilization.compareTo(BigDecimal.valueOf(90)) <= 0) {
            return "high";
        }
        if (utilization.compareTo(BigDecimal.valueOf(99)) <= 0) {
            return "critical";
        }
        return "full";
    }

    private long sumSafe(List<InventoryStock> stockList, java.util.function.Function<InventoryStock, Integer> extractor) {
        return stockList.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .mapToLong(Integer::longValue)
                .sum();
    }

    private String normalizeCode(String code) {
        return code == null || code.isBlank() ? null : code.trim();
    }

    private boolean isUnknownWarehouse(String warehouseCode) {
        String normalized = normalizeCode(warehouseCode);
        return normalized != null && warehouseRepository.findByCodeIgnoreCase(normalized).isEmpty();
    }

    private boolean isUnknownCategory(String categoryCode) {
        String normalized = normalizeCode(categoryCode);
        return normalized != null && categoryRepository.findByCodeIgnoreCase(normalized).isEmpty();
    }
}
