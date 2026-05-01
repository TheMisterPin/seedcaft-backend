package com.michele.mocks.controller;

import com.michele.mocks.dto.inventory.dashboard.BinHeatmapDashboardResponse;
import com.michele.mocks.dto.inventory.dashboard.CategoryDonutDashboardResponse;
import com.michele.mocks.dto.inventory.dashboard.InventoryDashboardResponse;
import com.michele.mocks.dto.inventory.dashboard.KpiDashboardResponse;
import com.michele.mocks.dto.inventory.dashboard.LineChartDashboardResponse;
import com.michele.mocks.dto.inventory.dashboard.LowStockTableDashboardResponse;
import com.michele.mocks.dto.inventory.dashboard.StockCompositionDashboardResponse;
import com.michele.mocks.dto.inventory.dashboard.TopBinsDashboardResponse;
import com.michele.mocks.service.InventoryDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory/dashboard")
public class InventoryDashboardController {

    private final InventoryDashboardService service;

    public InventoryDashboardController(InventoryDashboardService service) {
        this.service = service;
    }

    @Operation(
            summary = "Get inventory dashboard bundle",
            description = "Frontend-ready dashboard endpoint that returns chart-ready sections in one payload. "
                    + "Response shape is `meta` + `data` where `data` contains `series` and `sections` for direct UI rendering."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dashboard payload with meta and chart/table sections",
                    content = @Content(schema = @Schema(implementation = InventoryDashboardResponse.class)))
    })
    @GetMapping
    public InventoryDashboardResponse getDashboard(
            @Parameter(description = "Time range window for dashboard aggregation", schema = @Schema(defaultValue = "30d", allowableValues = {"7d", "30d", "90d"}))
            @RequestParam(defaultValue = "30d") String range,
            @Parameter(description = "Optional warehouse filter (warehouse code)")
            @RequestParam(required = false) String warehouseCode,
            @Parameter(description = "Optional category filter (category code)")
            @RequestParam(required = false) String categoryCode,
            @Parameter(description = "Maximum number of items for ranked/list sections", schema = @Schema(defaultValue = "10"))
            @RequestParam(defaultValue = "10") Integer limit) {
        return service.getDashboard(range, warehouseCode, categoryCode, limit);
    }

    @Operation(
            summary = "Get KPI cards",
            description = "Frontend-ready endpoint for KPI cards. Response uses dashboard schema conventions with `meta` and KPI `data` blocks."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "KPI cards payload",
                    content = @Content(schema = @Schema(implementation = KpiDashboardResponse.class)))
    })
    @GetMapping("/kpis")
    public KpiDashboardResponse getKpis(
            @Parameter(description = "Time range window for KPI calculation", schema = @Schema(defaultValue = "30d", allowableValues = {"7d", "30d", "90d"}))
            @RequestParam(defaultValue = "30d") String range,
            @Parameter(description = "Optional warehouse filter (warehouse code)")
            @RequestParam(required = false) String warehouseCode,
            @Parameter(description = "Optional category filter (category code)")
            @RequestParam(required = false) String categoryCode) {
        return service.getKpis(range, warehouseCode, categoryCode);
    }

    @Operation(
            summary = "Get category donut chart",
            description = "Frontend-ready endpoint for donut chart sections. Response is designed for direct visualization binding (`meta` + `data/sections`)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category donut payload",
                    content = @Content(schema = @Schema(implementation = CategoryDonutDashboardResponse.class)))
    })
    @GetMapping("/category-donut")
    public CategoryDonutDashboardResponse getCategoryDonut(
            @Parameter(description = "Time range window for donut chart", schema = @Schema(defaultValue = "30d", allowableValues = {"7d", "30d", "90d"}))
            @RequestParam(defaultValue = "30d") String range,
            @Parameter(description = "Optional warehouse filter (warehouse code)")
            @RequestParam(required = false) String warehouseCode,
            @Parameter(description = "Optional category filter (category code)")
            @RequestParam(required = false) String categoryCode) {
        return service.getCategoryDonut(range, warehouseCode, categoryCode);
    }

    @Operation(
            summary = "Get warehouse fill trend line",
            description = "Frontend-ready line-chart endpoint. Returns chart series and labels using dashboard response conventions (`meta` + `data/series`)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Warehouse fill trend line payload",
                    content = @Content(schema = @Schema(implementation = LineChartDashboardResponse.class)))
    })
    @GetMapping("/warehouse-fill-line")
    public LineChartDashboardResponse getWarehouseFillLine(
            @Parameter(description = "Time range window for trend line", schema = @Schema(defaultValue = "30d", allowableValues = {"7d", "30d", "90d"}))
            @RequestParam(defaultValue = "30d") String range,
            @Parameter(description = "Optional warehouse filter (warehouse code)")
            @RequestParam(required = false) String warehouseCode,
            @Parameter(description = "Optional category filter (category code)")
            @RequestParam(required = false) String categoryCode) {
        return service.getWarehouseFillLine(range, warehouseCode, categoryCode);
    }

    @Operation(
            summary = "Get stock composition breakdown",
            description = "Frontend-ready composition endpoint returning dashboard-friendly `meta` and `data/sections` payloads for cards/charts."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock composition payload",
                    content = @Content(schema = @Schema(implementation = StockCompositionDashboardResponse.class)))
    })
    @GetMapping("/stock-composition")
    public StockCompositionDashboardResponse getStockComposition(
            @Parameter(description = "Time range window for composition", schema = @Schema(defaultValue = "30d", allowableValues = {"7d", "30d", "90d"}))
            @RequestParam(defaultValue = "30d") String range,
            @Parameter(description = "Optional warehouse filter (warehouse code)")
            @RequestParam(required = false) String warehouseCode,
            @Parameter(description = "Optional category filter (category code)")
            @RequestParam(required = false) String categoryCode) {
        return service.getStockComposition(range, warehouseCode, categoryCode);
    }

    @Operation(
            summary = "Get top bins list",
            description = "Frontend-ready endpoint for ranked bin sections. Includes list-size control via `limit` and returns table/series-ready data."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top bins payload",
                    content = @Content(schema = @Schema(implementation = TopBinsDashboardResponse.class)))
    })
    @GetMapping("/top-bins")
    public TopBinsDashboardResponse getTopBins(
            @Parameter(description = "Optional warehouse filter (warehouse code)")
            @RequestParam(required = false) String warehouseCode,
            @Parameter(description = "Optional category filter (category code)")
            @RequestParam(required = false) String categoryCode,
            @Parameter(description = "Maximum number of bins returned", schema = @Schema(defaultValue = "10"))
            @RequestParam(defaultValue = "10") Integer limit) {
        return service.getTopBins(null, warehouseCode, categoryCode, limit);
    }

    @Operation(
            summary = "Get bin heatmap",
            description = "Frontend-ready heatmap endpoint that returns matrix-friendly `series/sections` data with shared dashboard metadata."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bin heatmap payload",
                    content = @Content(schema = @Schema(implementation = BinHeatmapDashboardResponse.class)))
    })
    @GetMapping("/bin-heatmap")
    public BinHeatmapDashboardResponse getBinHeatmap(
            @Parameter(description = "Optional warehouse filter (warehouse code)")
            @RequestParam(required = false) String warehouseCode,
            @Parameter(description = "Optional category filter (category code)")
            @RequestParam(required = false) String categoryCode,
            @Parameter(description = "Maximum number of bins/cells returned", schema = @Schema(defaultValue = "10"))
            @RequestParam(defaultValue = "10") Integer limit) {
        return service.getBinHeatmap(null, warehouseCode, categoryCode, limit);
    }

    @Operation(
            summary = "Get low-stock table",
            description = "Frontend-ready table endpoint for low-stock alerts with dashboard `meta` and table/section data."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Low-stock table payload",
                    content = @Content(schema = @Schema(implementation = LowStockTableDashboardResponse.class)))
    })
    @GetMapping("/low-stock")
    public LowStockTableDashboardResponse getLowStock(
            @Parameter(description = "Optional warehouse filter (warehouse code)")
            @RequestParam(required = false) String warehouseCode,
            @Parameter(description = "Optional category filter (category code)")
            @RequestParam(required = false) String categoryCode,
            @Parameter(description = "Maximum number of table rows returned", schema = @Schema(defaultValue = "10"))
            @RequestParam(defaultValue = "10") Integer limit) {
        return service.getLowStock(null, warehouseCode, categoryCode, limit);
    }

    @Operation(
            summary = "Get inventory value trend line",
            description = "Frontend-ready time-series endpoint returning `meta` + `data/series` payloads for line-chart visualization."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory value line payload",
                    content = @Content(schema = @Schema(implementation = LineChartDashboardResponse.class)))
    })
    @GetMapping("/inventory-value-line")
    public LineChartDashboardResponse getInventoryValueLine(
            @Parameter(description = "Time range window for inventory value trend", schema = @Schema(defaultValue = "30d", allowableValues = {"7d", "30d", "90d"}))
            @RequestParam(defaultValue = "30d") String range,
            @Parameter(description = "Optional warehouse filter (warehouse code)")
            @RequestParam(required = false) String warehouseCode,
            @Parameter(description = "Optional category filter (category code)")
            @RequestParam(required = false) String categoryCode) {
        return service.getInventoryValueLine(range, warehouseCode, categoryCode);
    }
}
