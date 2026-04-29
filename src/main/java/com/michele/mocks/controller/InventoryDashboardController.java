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

    @GetMapping
    public InventoryDashboardResponse getDashboard(
            @RequestParam(defaultValue = "30d") String range,
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String categoryCode,
            @RequestParam(defaultValue = "10") Integer limit) {
        return service.getDashboard(range, warehouseCode, categoryCode, limit);
    }

    @GetMapping("/kpis")
    public KpiDashboardResponse getKpis(
            @RequestParam(defaultValue = "30d") String range,
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String categoryCode) {
        return service.getKpis(range, warehouseCode, categoryCode);
    }

    @GetMapping("/category-donut")
    public CategoryDonutDashboardResponse getCategoryDonut(
            @RequestParam(defaultValue = "30d") String range,
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String categoryCode) {
        return service.getCategoryDonut(range, warehouseCode, categoryCode);
    }

    @GetMapping("/warehouse-fill-line")
    public LineChartDashboardResponse getWarehouseFillLine(
            @RequestParam(defaultValue = "30d") String range,
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String categoryCode) {
        return service.getWarehouseFillLine(range, warehouseCode, categoryCode);
    }

    @GetMapping("/stock-composition")
    public StockCompositionDashboardResponse getStockComposition(
            @RequestParam(defaultValue = "30d") String range,
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String categoryCode) {
        return service.getStockComposition(range, warehouseCode, categoryCode);
    }

    @GetMapping("/top-bins")
    public TopBinsDashboardResponse getTopBins(
            @RequestParam(defaultValue = "30d") String range,
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String categoryCode,
            @RequestParam(defaultValue = "10") Integer limit) {
        return service.getTopBins(range, warehouseCode, categoryCode, limit);
    }

    @GetMapping("/bin-heatmap")
    public BinHeatmapDashboardResponse getBinHeatmap(
            @RequestParam(defaultValue = "30d") String range,
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String categoryCode,
            @RequestParam(defaultValue = "10") Integer limit) {
        return service.getBinHeatmap(range, warehouseCode, categoryCode, limit);
    }

    @GetMapping("/low-stock")
    public LowStockTableDashboardResponse getLowStock(
            @RequestParam(defaultValue = "30d") String range,
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String categoryCode,
            @RequestParam(defaultValue = "10") Integer limit) {
        return service.getLowStock(range, warehouseCode, categoryCode, limit);
    }

    @GetMapping("/inventory-value-line")
    public LineChartDashboardResponse getInventoryValueLine(
            @RequestParam(defaultValue = "30d") String range,
            @RequestParam(required = false) String warehouseCode,
            @RequestParam(required = false) String categoryCode) {
        return service.getInventoryValueLine(range, warehouseCode, categoryCode);
    }
}
