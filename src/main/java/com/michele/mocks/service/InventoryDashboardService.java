package com.michele.mocks.service;

import com.michele.mocks.dto.inventory.dashboard.*;
import com.michele.mocks.entity.enums.MetricScopeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InventoryDashboardService {

    private final InventoryMetricService inventoryMetricService;
    private final InventoryStockService inventoryStockService;

    public LowStockTableDashboardResponse buildLowStockTable(String scopeCode, Integer limit) {
        int resolvedLimit = limit == null || limit <= 0 ? 10 : limit;
        DashboardMetaResponse meta = defaultMeta("Low Stock", DashboardType.TABLE, MetricScopeType.WAREHOUSE, scopeCode);
        List<TableColumnResponse> columns = List.of(
                new TableColumnResponse("label", "Item"),
                new TableColumnResponse("value", "Available"),
                new TableColumnResponse("trendValue", "Reorder Gap")
        );
        return new LowStockTableDashboardResponse(meta, columns, inventoryStockService.getLowStockTableRows(resolvedLimit));
    }

    public TopBinsDashboardResponse buildTopBins(String scopeCode, Integer limit) {
        int resolvedLimit = limit == null || limit <= 0 ? 10 : limit;
        DashboardMetaResponse meta = defaultMeta("Top Bins", DashboardType.BAR, MetricScopeType.WAREHOUSE, scopeCode);
        return new TopBinsDashboardResponse(meta, inventoryStockService.getTopBinsData(resolvedLimit));
    }

    public BinHeatmapDashboardResponse buildBinHeatmap(String scopeCode) {
        DashboardMetaResponse meta = defaultMeta("Bin Utilization Heatmap", DashboardType.HEATMAP, MetricScopeType.WAREHOUSE, scopeCode);
        return new BinHeatmapDashboardResponse(meta, inventoryStockService.getBinHeatmapData(scopeCode));
    }

    public StockCompositionDashboardResponse buildStockComposition(String scopeCode) {
        List<DashboardDataPointResponse> segments = inventoryStockService.getStockCompositionData(scopeCode);
        BigDecimal total = segments.stream()
                .map(DashboardDataPointResponse::value)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        DashboardMetaResponse meta = defaultMeta("Stock Composition", DashboardType.DONUT, MetricScopeType.WAREHOUSE, scopeCode);
        return new StockCompositionDashboardResponse(meta, total, segments);
    }

    public KpiDashboardResponse buildKpis(String range, String scopeCode) {
        String resolvedRange = range == null || range.isBlank() ? "30d" : range;
        int rangeDays = inventoryMetricService.parseRangeDays(resolvedRange);

        var latest = inventoryMetricService.findLatestSnapshot(MetricScopeType.WAREHOUSE, scopeCode).orElse(null);
        long latestUnits = latest == null || latest.getTotalUnits() == null ? 0L : latest.getTotalUnits();
        var rangeSnapshots = inventoryMetricService.findSnapshotsInRange(MetricScopeType.WAREHOUSE, scopeCode, resolvedRange);

        BigDecimal trend = inventoryMetricService.calculateTrendPercentage(rangeSnapshots, latestUnits);

        DashboardDataPointResponse totalUnits = new DashboardDataPointResponse(
                "Total Units",
                BigDecimal.valueOf(latestUnits),
                latestUnits + " units",
                null,
                trend,
                trend.signum() > 0 ? "up" : trend.signum() < 0 ? "down" : "flat"
        );

        DashboardMetaResponse meta = defaultMeta("Inventory KPI (" + rangeDays + "d)", DashboardType.KPI, MetricScopeType.WAREHOUSE, scopeCode);
        return new KpiDashboardResponse(meta, List.of(totalUnits));
    }

    public InventoryDashboardResponse buildFullDashboard(String range, Integer limit, String scopeCode) {
        String resolvedRange = range == null || range.isBlank() ? "30d" : range;
        Integer resolvedLimit = limit == null || limit <= 0 ? 10 : limit;

        Map<String, Object> sections = new LinkedHashMap<>();
        sections.put("kpi", buildKpis(resolvedRange, scopeCode));
        sections.put("lowStock", buildLowStockTable(scopeCode, resolvedLimit));
        sections.put("topBins", buildTopBins(scopeCode, resolvedLimit));
        sections.put("binHeatmap", buildBinHeatmap(scopeCode));
        sections.put("stockComposition", buildStockComposition(scopeCode));

        return new InventoryDashboardResponse(sections);
    }

    private DashboardMetaResponse defaultMeta(String title, DashboardType type, MetricScopeType scopeType, String scopeCode) {
        return new DashboardMetaResponse(
                title,
                type,
                Instant.now(),
                scopeType.name().toLowerCase(),
                scopeCode,
                scopeCode
        );
    }
}
