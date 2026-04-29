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
            order by (s.reorderPoint - s.quantityAvailable) desc, s.quantityAvailable asc
            """)
    List<InventoryStock> findLowStockByUrgency(Pageable pageable);
}
