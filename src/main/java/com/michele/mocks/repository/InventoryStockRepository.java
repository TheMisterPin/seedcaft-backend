package com.michele.mocks.repository;

import com.michele.mocks.entity.InventoryStock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
