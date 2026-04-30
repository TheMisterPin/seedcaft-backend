package com.michele.mocks.repository;

import com.michele.mocks.entity.InventoryStock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventoryStockRepository extends JpaRepository<InventoryStock, Long> {

    @Query("""
            select s
            from InventoryStock s
            where s.quantityAvailable <= s.reorderPoint
            order by
                case when s.quantityAvailable <= 0 then 0 else 1 end asc,
                case when s.reorderPoint <= 0 then 999999999.0 else (1.0 * s.quantityAvailable / s.reorderPoint) end asc,
                s.quantityAvailable asc
            """)
    List<InventoryStock> findLowStockByUrgency(Pageable pageable);

    @Query("""
            select s
            from InventoryStock s
            where s.quantityAvailable <= s.reorderPoint
              and (:warehouseCode is null or upper(s.warehouse.code) = upper(:warehouseCode))
              and (:categoryCode is null or upper(s.product.category.code) = upper(:categoryCode))
            order by
                case when s.quantityAvailable <= 0 then 0 else 1 end asc,
                case when s.reorderPoint <= 0 then 999999999.0 else (1.0 * s.quantityAvailable / s.reorderPoint) end asc,
                s.quantityAvailable asc
            """)
    List<InventoryStock> findLowStockByUrgencyFiltered(
            @Param("warehouseCode") String warehouseCode,
            @Param("categoryCode") String categoryCode,
            Pageable pageable
    );

}
