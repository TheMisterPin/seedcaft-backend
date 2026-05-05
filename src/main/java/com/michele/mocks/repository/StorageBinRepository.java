package com.michele.mocks.repository;

import com.michele.mocks.entity.StorageBin;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StorageBinRepository extends JpaRepository<StorageBin, Long> {

    List<StorageBin> findByWarehouseCodeIgnoreCase(String warehouseCode);

    List<StorageBin> findByWarehouseCodeIgnoreCase(String warehouseCode, Pageable pageable);

    @Query("""
            select b
            from StorageBin b
            where b.maxStorageUnits > 0
            order by (1.0 * b.currentStorageUnits / b.maxStorageUnits) desc, b.currentStorageUnits desc
            """)
    List<StorageBin> findTopByUtilization(Pageable pageable);

    @Query("""
            select distinct b
            from StorageBin b
            left join InventoryStock s on s.bin.id = b.id
            left join s.product p
            left join p.category c
            where b.maxStorageUnits > 0
              and (:warehouseCode is null or upper(b.warehouse.code) = upper(:warehouseCode))
              and (:categoryCode is null or upper(c.code) = upper(:categoryCode))
            order by (1.0 * b.currentStorageUnits / b.maxStorageUnits) desc, b.currentStorageUnits desc
            """)
    List<StorageBin> findTopByUtilizationFiltered(
            @Param("warehouseCode") String warehouseCode,
            @Param("categoryCode") String categoryCode,
            Pageable pageable
    );

}
