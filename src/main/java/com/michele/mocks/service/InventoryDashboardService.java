package com.michele.mocks.service;

import com.michele.mocks.dto.inventory.dashboard.BinHeatmapDashboardResponse;
import com.michele.mocks.dto.inventory.dashboard.CategoryDonutDashboardResponse;
import com.michele.mocks.dto.inventory.dashboard.InventoryDashboardResponse;
import com.michele.mocks.dto.inventory.dashboard.KpiDashboardResponse;
import com.michele.mocks.dto.inventory.dashboard.LineChartDashboardResponse;
import com.michele.mocks.dto.inventory.dashboard.LowStockTableDashboardResponse;
import com.michele.mocks.dto.inventory.dashboard.StockCompositionDashboardResponse;
import com.michele.mocks.dto.inventory.dashboard.TopBinsDashboardResponse;

public interface InventoryDashboardService {

    InventoryDashboardResponse getDashboard(String range, String warehouseCode, String categoryCode, Integer limit);

    KpiDashboardResponse getKpis(String range, String warehouseCode, String categoryCode);

    CategoryDonutDashboardResponse getCategoryDonut(String range, String warehouseCode, String categoryCode);

    LineChartDashboardResponse getWarehouseFillLine(String range, String warehouseCode, String categoryCode);

    StockCompositionDashboardResponse getStockComposition(String range, String warehouseCode, String categoryCode);

    TopBinsDashboardResponse getTopBins(String range, String warehouseCode, String categoryCode, Integer limit);

    BinHeatmapDashboardResponse getBinHeatmap(String range, String warehouseCode, String categoryCode, Integer limit);

    LowStockTableDashboardResponse getLowStock(String range, String warehouseCode, String categoryCode, Integer limit);

    LineChartDashboardResponse getInventoryValueLine(String range, String warehouseCode, String categoryCode);
}
