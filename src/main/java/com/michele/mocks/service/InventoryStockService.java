package com.michele.mocks.service;

import com.michele.mocks.dto.inventory.dashboard.DashboardDataPointResponse;
import com.michele.mocks.entity.InventoryStock;
import com.michele.mocks.entity.StorageBin;
import com.michele.mocks.entity.Warehouse;
import com.michele.mocks.exception.ResourceNotFoundException;
import com.michele.mocks.repository.InventoryStockRepository;
import com.michele.mocks.repository.StorageBinRepository;
import com.michele.mocks.repository.WarehouseRepository;
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

    public List<DashboardDataPointResponse> getLowStockTableRows(int limit) {
        int size = limit > 0 ? limit : 10;
        return inventoryStockRepository.findLowStockByUrgency(PageRequest.of(0, size))
                .stream()
                .map(this::toLowStockDataPoint)
                .toList();
    }

    public List<DashboardDataPointResponse> getTopBinsData(int limit) {
        int size = limit > 0 ? limit : 10;
        return storageBinRepository.findTopByUtilization(PageRequest.of(0, size))
                .stream()
                .map(this::toTopBinDataPoint)
                .toList();
    }

    public List<DashboardDataPointResponse> getBinHeatmapData(String warehouseCode) {
        List<StorageBin> bins = warehouseCode == null || warehouseCode.isBlank()
                ? storageBinRepository.findAll()
                : storageBinRepository.findByWarehouseCodeIgnoreCase(warehouseCode.trim());

        return bins.stream()
                .map(this::toHeatmapDataPoint)
                .toList();
    }

    public List<DashboardDataPointResponse> getStockCompositionData(String warehouseCode) {
        List<InventoryStock> stockList = warehouseCode == null || warehouseCode.isBlank()
                ? inventoryStockRepository.findAll()
                : inventoryStockRepository.findAll().stream()
                .filter(stock -> stock.getWarehouse().getCode().equalsIgnoreCase(warehouseCode.trim()))
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

    private DashboardDataPointResponse toLowStockDataPoint(InventoryStock stock) {
        String label = stock.getProduct().getName() + " @ " + stock.getWarehouse().getCode();
        return new DashboardDataPointResponse(
                label,
                BigDecimal.valueOf(stock.getQuantityAvailable()),
                DashboardFormatters.formatInteger(stock.getQuantityAvailable()) + " units",
                null,
                BigDecimal.valueOf(stock.getReorderPoint() - stock.getQuantityAvailable()),
                stock.getQuantityAvailable() <= 0 ? "down" : "flat",
                null,
                null,
                null,
                null
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
                null,
                null,
                null,
                BigDecimal.valueOf(Objects.requireNonNullElse(bin.getCurrentStorageUnits(), 0)),
                DashboardFormatters.formatInteger(Objects.requireNonNullElse(bin.getCurrentStorageUnits(), 0))
                        + "/" + DashboardFormatters.formatInteger(Objects.requireNonNullElse(bin.getMaxStorageUnits(), 0)),
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
                BigDecimal.valueOf(Objects.requireNonNullElse(bin.getCurrentStorageUnits(), 0)),
                DashboardFormatters.formatPercentage(utilization),
                utilization,
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
                percentage,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private long sumSafe(List<InventoryStock> stockList, java.util.function.Function<InventoryStock, Integer> extractor) {
        return stockList.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .mapToLong(Integer::longValue)
                .sum();
    }
}
