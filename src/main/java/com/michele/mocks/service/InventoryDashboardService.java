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
                new TableColumnResponse("sku", "sku"),
                new TableColumnResponse("productName", "productName"),
                new TableColumnResponse("categoryName", "categoryName"),
                new TableColumnResponse("warehouseCode", "warehouseCode"),
                new TableColumnResponse("availableUnits", "availableUnits"),
                new TableColumnResponse("reorderPoint", "reorderPoint"),
                new TableColumnResponse("status", "status")
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
        BigDecimal totalValue = segments.stream()
                .map(DashboardDataPointResponse::value)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        DashboardMetaResponse meta = buildMeta("Stock Composition", DashboardType.PROGRESS, "Current stock split by tracked segments.", null, scopeCode, null, null);
        StockCompositionDashboardResponse.TotalResponse total = new StockCompositionDashboardResponse.TotalResponse(
                totalValue,
                DashboardFormatters.formatInteger(totalValue.longValue()) + " units"
        );
        return new StockCompositionDashboardResponse(meta, total, segments);
    }

    public KpiDashboardResponse buildKpis(String range, String scopeCode) {
        MetricRange metricRange = MetricRange.parse(range);
        MetricScopeType scopeType = scopeCode == null || scopeCode.isBlank()
                ? MetricScopeType.GLOBAL
                : MetricScopeType.WAREHOUSE;
        var latest = inventoryMetricService.findLatestSnapshot(scopeType, scopeCode).orElse(null);
        var rangeSnapshots = inventoryMetricService.findSnapshotsInRange(scopeType, scopeCode, metricRange.code());

        DashboardDataPointResponse totalUnits = buildKpiDataPoint(
                "totalUnits", latest == null ? null : latest.getTotalUnits(), "units", rangeSnapshots,
                snapshot -> snapshot.getTotalUnits() == null ? null : BigDecimal.valueOf(snapshot.getTotalUnits())
        );
        DashboardDataPointResponse availableUnits = buildKpiDataPoint(
                "availableUnits", latest == null ? null : latest.getAvailableUnits(), "units", rangeSnapshots,
                snapshot -> snapshot.getAvailableUnits() == null ? null : BigDecimal.valueOf(snapshot.getAvailableUnits())
        );
        DashboardDataPointResponse reservedUnits = buildKpiDataPoint(
                "reservedUnits", latest == null ? null : latest.getReservedUnits(), "units", rangeSnapshots,
                snapshot -> snapshot.getReservedUnits() == null ? null : BigDecimal.valueOf(snapshot.getReservedUnits())
        );
        DashboardDataPointResponse blockedUnits = buildKpiDataPoint(
                "blockedUnits", latest == null ? null : latest.getBlockedUnits(), "units", rangeSnapshots,
                snapshot -> snapshot.getBlockedUnits() == null ? null : BigDecimal.valueOf(snapshot.getBlockedUnits())
        );
        DashboardDataPointResponse fillPercentage = buildKpiDataPoint(
                "fillPercentage", latest == null ? null : latest.getFillPercentage(), "%", rangeSnapshots, InventoryMetricSnapshot::getFillPercentage
        );
        DashboardDataPointResponse inventoryValue = buildKpiDataPoint(
                "inventoryValue", latest == null ? null : latest.getInventoryValue(), "currency", rangeSnapshots, InventoryMetricSnapshot::getInventoryValue
        );

        DashboardMetaResponse meta = buildMeta("Inventory KPI (" + metricRange.code() + ")", DashboardType.KPI, "KPI summary for the selected time range.", metricRange.code(), scopeCode, null, null);
        return new KpiDashboardResponse(meta, List.of(
                totalUnits, availableUnits, reservedUnits, blockedUnits, fillPercentage, inventoryValue
        ));
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
                        "currency",
                        snapshot.getFillPercentage() == null ? BigDecimal.ZERO : snapshot.getFillPercentage(),
                        null,
                        null,
                        null,
                        null,
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
                                        "%",
                                        snapshot.getFillPercentage() == null ? BigDecimal.ZERO : snapshot.getFillPercentage(),
                                        null,
                                        null,
                                        null,
                                        null,
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
                        "currency",
                        null,
                        null,
                        null,
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

    private DashboardDataPointResponse buildKpiDataPoint(
            String label,
            Number numericValue,
            String unit,
            List<InventoryMetricSnapshot> rangeSnapshots,
            java.util.function.Function<InventoryMetricSnapshot, BigDecimal> trendBaselineExtractor
    ) {
        BigDecimal value = numericValue == null ? BigDecimal.ZERO : BigDecimal.valueOf(numericValue.doubleValue());
        BigDecimal trend = inventoryMetricService.calculateTrendPercentage(rangeSnapshots, value, trendBaselineExtractor);
        String formattedValue = switch (label) {
            case "fillPercentage" -> DashboardFormatters.formatPercentage(value);
            case "inventoryValue" -> DashboardFormatters.formatCurrency(value);
            default -> DashboardFormatters.formatInteger(value.longValue()) + " units";
        };
        return new DashboardDataPointResponse(
                label,
                value,
                formattedValue,
                unit,
                null,
                trend,
                trend.signum() > 0 ? "up" : trend.signum() < 0 ? "down" : "flat",
                null,
                null,
                null,
                null
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
