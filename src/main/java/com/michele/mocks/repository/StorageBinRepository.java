package com.michele.mocks.repository;

import com.michele.mocks.entity.StorageBin;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
