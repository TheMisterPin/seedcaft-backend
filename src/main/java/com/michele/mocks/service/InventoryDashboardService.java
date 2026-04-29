package com.michele.mocks.service;

import com.michele.mocks.dto.inventory.dashboard.*;
import com.michele.mocks.entity.enums.MetricScopeType;
import com.michele.mocks.exception.BadRequestException;
import com.michele.mocks.util.DashboardFormatters;
import com.michele.mocks.util.MetricRange;
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
        int resolvedLimit = validateLimit(limit);
        DashboardMetaResponse meta = defaultMeta("Low Stock", DashboardType.TABLE, MetricScopeType.WAREHOUSE, scopeCode);
        List<TableColumnResponse> columns = List.of(
                new TableColumnResponse("label", "Item"),
                new TableColumnResponse("value", "Available"),
                new TableColumnResponse("trendValue", "Reorder Gap")
        );
        return new LowStockTableDashboardResponse(meta, columns, inventoryStockService.getLowStockTableRows(resolvedLimit));
    }

    public TopBinsDashboardResponse buildTopBins(String scopeCode, Integer limit) {
        int resolvedLimit = validateLimit(limit);
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
        MetricRange metricRange = MetricRange.parse(range);

        var latest = inventoryMetricService.findLatestSnapshot(MetricScopeType.WAREHOUSE, scopeCode).orElse(null);
        long latestUnits = latest == null || latest.getTotalUnits() == null ? 0L : latest.getTotalUnits();
        var rangeSnapshots = inventoryMetricService.findSnapshotsInRange(MetricScopeType.WAREHOUSE, scopeCode, metricRange.code());

        BigDecimal trend = inventoryMetricService.calculateTrendPercentage(rangeSnapshots, latestUnits);

        DashboardDataPointResponse totalUnits = new DashboardDataPointResponse(
                "Total Units",
                BigDecimal.valueOf(latestUnits),
                DashboardFormatters.formatInteger(latestUnits) + " units",
                null,
                trend,
                trend.signum() > 0 ? "up" : trend.signum() < 0 ? "down" : "flat"
        );

        DashboardMetaResponse meta = defaultMeta("Inventory KPI (" + metricRange.code() + ")", DashboardType.KPI, MetricScopeType.WAREHOUSE, scopeCode);
        return new KpiDashboardResponse(meta, List.of(totalUnits));
    }

    public InventoryDashboardResponse buildFullDashboard(String range, Integer limit, String scopeCode) {
        MetricRange metricRange = MetricRange.parse(range);
        int resolvedLimit = validateLimit(limit);

        Map<String, Object> sections = new LinkedHashMap<>();
        sections.put("kpi", buildKpis(metricRange.code(), scopeCode));
        sections.put("lowStock", buildLowStockTable(scopeCode, resolvedLimit));
        sections.put("topBins", buildTopBins(scopeCode, resolvedLimit));
        sections.put("binHeatmap", buildBinHeatmap(scopeCode));
        sections.put("stockComposition", buildStockComposition(scopeCode));

        return new InventoryDashboardResponse(sections);
    }

    public InventoryDashboardResponse getDashboard(String range, String warehouseCode, String categoryCode, Integer limit) {
        return buildFullDashboard(range, limit, warehouseCode);
    }

    public KpiDashboardResponse getKpis(String range, String warehouseCode, String categoryCode) {
        return buildKpis(range, warehouseCode);
    }

    public CategoryDonutDashboardResponse getCategoryDonut(String range, String warehouseCode, String categoryCode) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public LineChartDashboardResponse getWarehouseFillLine(String range, String warehouseCode, String categoryCode) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public StockCompositionDashboardResponse getStockComposition(String range, String warehouseCode, String categoryCode) {
        return buildStockComposition(warehouseCode);
    }

    public TopBinsDashboardResponse getTopBins(String range, String warehouseCode, String categoryCode, Integer limit) {
        return buildTopBins(warehouseCode, limit);
    }

    public BinHeatmapDashboardResponse getBinHeatmap(String range, String warehouseCode, String categoryCode, Integer limit) {
        return buildBinHeatmap(warehouseCode);
    }

    public LowStockTableDashboardResponse getLowStock(String range, String warehouseCode, String categoryCode, Integer limit) {
        return buildLowStockTable(warehouseCode, limit);
    }

    public LineChartDashboardResponse getInventoryValueLine(String range, String warehouseCode, String categoryCode) {
        throw new UnsupportedOperationException("Not implemented");
    }

    private int validateLimit(Integer limit) {
        if (limit == null) {
            return 10;
        }
        if (limit <= 0) {
            throw new BadRequestException("Invalid limit. Allowed values are positive integers.");
        }
        return limit;
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
