package com.michele.mocks.service;

import com.michele.mocks.dto.inventory.dashboard.*;
import com.michele.mocks.entity.InventoryMetricSnapshot;
import com.michele.mocks.entity.enums.MetricScopeType;
import com.michele.mocks.exception.BadRequestException;
import com.michele.mocks.util.DashboardFormatters;
import com.michele.mocks.util.MetricRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryDashboardService {

    private final InventoryMetricService inventoryMetricService;
    private final InventoryStockService inventoryStockService;

    public LowStockTableDashboardResponse buildLowStockTable(String scopeCode, Integer limit) {
        int resolvedLimit = validateLimit(limit);
        DashboardMetaResponse meta = buildMeta("Low Stock", DashboardType.TABLE, "Items that are below reorder threshold.", null, scopeCode, null, resolvedLimit);
        List<TableColumnResponse> columns = List.of(
                new TableColumnResponse("label", "Item"),
                new TableColumnResponse("value", "Available"),
                new TableColumnResponse("trendValue", "Reorder Gap")
        );
        return new LowStockTableDashboardResponse(meta, columns, inventoryStockService.getLowStockTableRows(resolvedLimit));
    }

    public TopBinsDashboardResponse buildTopBins(String scopeCode, Integer limit) {
        int resolvedLimit = validateLimit(limit);
        DashboardMetaResponse meta = buildMeta("Top Bins", DashboardType.BAR, "Bins with the highest utilization.", null, scopeCode, null, resolvedLimit);
        return new TopBinsDashboardResponse(meta, inventoryStockService.getTopBinsData(resolvedLimit));
    }

    public BinHeatmapDashboardResponse buildBinHeatmap(String scopeCode) {
        DashboardMetaResponse meta = buildMeta("Bin Utilization Heatmap", DashboardType.HEATMAP, "Utilization distribution across warehouse bins.", null, scopeCode, null, null);
        return new BinHeatmapDashboardResponse(meta, inventoryStockService.getBinHeatmapData(scopeCode));
    }

    public StockCompositionDashboardResponse buildStockComposition(String scopeCode) {
        List<DashboardDataPointResponse> segments = inventoryStockService.getStockCompositionData(scopeCode);
        BigDecimal total = segments.stream()
                .map(DashboardDataPointResponse::value)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        DashboardMetaResponse meta = buildMeta("Stock Composition", DashboardType.DONUT, "Current stock split by tracked segments.", null, scopeCode, null, null);
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

        DashboardMetaResponse meta = buildMeta("Inventory KPI (" + metricRange.code() + ")", DashboardType.KPI, "KPI summary for the selected time range.", metricRange.code(), scopeCode, null, null);
        return new KpiDashboardResponse(meta, List.of(totalUnits));
    }

    public InventoryDashboardResponse buildFullDashboard(String range, Integer limit, String scopeCode) {
        MetricRange metricRange = MetricRange.parse(range);
        int resolvedLimit = validateLimit(limit);

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("range", metricRange.code());
        meta.put("limit", resolvedLimit);

        Map<String, Object> sections = new LinkedHashMap<>();
        sections.put("kpis", buildKpis(metricRange.code(), scopeCode));
        sections.put("categoryDonut", getCategoryDonut(metricRange.code(), scopeCode, null));
        sections.put("warehouseFillLine", getWarehouseFillLine(metricRange.code(), scopeCode, null));
        sections.put("stockComposition", getStockComposition(metricRange.code(), scopeCode, null));
        sections.put("topBins", getTopBins(metricRange.code(), scopeCode, null, resolvedLimit));
        sections.put("binHeatmap", getBinHeatmap(metricRange.code(), scopeCode, null, resolvedLimit));
        sections.put("lowStock", getLowStock(metricRange.code(), scopeCode, null, resolvedLimit));
        sections.put("inventoryValueLine", getInventoryValueLine(metricRange.code(), scopeCode, null));

        return new InventoryDashboardResponse(meta, sections);
    }

    public InventoryDashboardResponse getDashboard(String range, String warehouseCode, String categoryCode, Integer limit) {
        return buildFullDashboard(range, limit, warehouseCode);
    }

    public KpiDashboardResponse getKpis(String range, String warehouseCode, String categoryCode) {
        return buildKpis(range, warehouseCode);
    }

    public CategoryDonutDashboardResponse getCategoryDonut(String range, String warehouseCode, String categoryCode) {
        MetricRange metricRange = MetricRange.parse(range);
        List<DashboardDataPointResponse> data = inventoryMetricService
                .findSnapshotsInRange(MetricScopeType.CATEGORY, null, metricRange.code())
                .stream()
                .collect(Collectors.toMap(
                        snapshot -> snapshot.getScopeCode().toUpperCase(),
                        snapshot -> snapshot,
                        (left, right) -> left.getSnapshotDate().isAfter(right.getSnapshotDate()) ? left : right
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing((InventoryMetricSnapshot s) -> s.getInventoryValue(), Comparator.nullsLast(BigDecimal::compareTo)).reversed())
                .toList()
                .stream()
                .map(snapshot -> new DashboardDataPointResponse(
                        snapshot.getScopeName(),
                        snapshot.getInventoryValue() == null ? BigDecimal.ZERO : snapshot.getInventoryValue(),
                        DashboardFormatters.formatCurrency(snapshot.getInventoryValue()),
                        snapshot.getFillPercentage() == null ? BigDecimal.ZERO : snapshot.getFillPercentage(),
                        null,
                        null
                ))
                .toList();

        DashboardMetaResponse meta = buildMeta(
                "Category Donut (" + metricRange.code() + ")",
                DashboardType.DONUT,
                "Inventory value distribution by category.",
                metricRange.code(),
                warehouseCode,
                categoryCode,
                null
        );
        return new CategoryDonutDashboardResponse(meta, data);
    }

    public LineChartDashboardResponse getWarehouseFillLine(String range, String warehouseCode, String categoryCode) {
        MetricRange metricRange = MetricRange.parse(range);
        List<LineChartSeriesResponse> series = inventoryMetricService
                .findSnapshotsInRange(MetricScopeType.WAREHOUSE, warehouseCode, metricRange.code())
                .stream()
                .collect(Collectors.groupingBy(
                        snapshot -> snapshot.getScopeName() == null ? snapshot.getScopeCode() : snapshot.getScopeName(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .entrySet()
                .stream()
                .map(entry -> new LineChartSeriesResponse(
                        entry.getKey(),
                        entry.getValue().stream()
                                .sorted(Comparator.comparing(InventoryMetricSnapshot::getSnapshotDate))
                                .map(snapshot -> new DashboardDataPointResponse(
                                        DashboardFormatters.formatDate(snapshot.getSnapshotDate()),
                                        snapshot.getFillPercentage() == null ? BigDecimal.ZERO : snapshot.getFillPercentage(),
                                        DashboardFormatters.formatPercentage(snapshot.getFillPercentage()),
                                        snapshot.getFillPercentage() == null ? BigDecimal.ZERO : snapshot.getFillPercentage(),
                                        null,
                                        null
                                ))
                                .toList()
                ))
                .toList();

        DashboardMetaResponse meta = buildMeta(
                "Warehouse Fill Trend (" + metricRange.code() + ")",
                DashboardType.LINE,
                "Warehouse fill percentage trend over time.",
                metricRange.code(),
                warehouseCode,
                categoryCode,
                null
        );
        return new LineChartDashboardResponse(meta, "date", "fillPercentage", series);
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
        MetricRange metricRange = MetricRange.parse(range);
        MetricScopeType scopeType = warehouseCode == null || warehouseCode.isBlank()
                ? MetricScopeType.GLOBAL
                : MetricScopeType.WAREHOUSE;

        List<InventoryMetricSnapshot> snapshots = inventoryMetricService
                .findSnapshotsInRange(scopeType, warehouseCode, metricRange.code());

        String seriesName = snapshots.stream()
                .findFirst()
                .map(snapshot -> snapshot.getScopeName() == null ? snapshot.getScopeCode() : snapshot.getScopeName())
                .orElse(scopeType == MetricScopeType.GLOBAL ? "Global" : warehouseCode);

        List<DashboardDataPointResponse> points = snapshots.stream()
                .sorted(Comparator.comparing(InventoryMetricSnapshot::getSnapshotDate))
                .map(snapshot -> new DashboardDataPointResponse(
                        DashboardFormatters.formatDate(snapshot.getSnapshotDate()),
                        snapshot.getInventoryValue() == null ? BigDecimal.ZERO : snapshot.getInventoryValue(),
                        DashboardFormatters.formatCurrency(snapshot.getInventoryValue()),
                        null,
                        null,
                        null
                ))
                .toList();

        DashboardMetaResponse meta = buildMeta(
                "Inventory Value Trend (" + metricRange.code() + ")",
                DashboardType.LINE,
                "Inventory value trend for the selected scope.",
                metricRange.code(),
                warehouseCode,
                categoryCode,
                null
        );
        return new LineChartDashboardResponse(
                meta,
                "date",
                "inventoryValue",
                List.of(new LineChartSeriesResponse(seriesName, points))
        );
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

    private DashboardMetaResponse buildMeta(
            String title,
            DashboardType type,
            String description,
            String range,
            String warehouseCode,
            String categoryCode,
            Integer limit
    ) {
        String scope = categoryCode != null && !categoryCode.isBlank()
                ? MetricScopeType.CATEGORY.name().toLowerCase()
                : warehouseCode != null && !warehouseCode.isBlank()
                ? MetricScopeType.WAREHOUSE.name().toLowerCase()
                : MetricScopeType.GLOBAL.name().toLowerCase();

        return new DashboardMetaResponse(
                title,
                type,
                Instant.now(),
                description,
                scope,
                range,
                warehouseCode,
                categoryCode,
                limit
        );
    }
}
