package bbt.tao.warehouse.repository;

import bbt.tao.warehouse.model.InventoryCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryCountRepository extends JpaRepository<InventoryCount, Long> {

    List<InventoryCount> findByInventory_Id(Long inventoryId);

    List<InventoryCount> findByProduct_Id(Long productId);

    List<InventoryCount> findByLocation_Id(Long locationId);

    List<InventoryCount> findByCountedBy_Id(Long userId);

    @Query("SELECT ic FROM InventoryCount ic WHERE ic.expectedQuantity <> ic.actualQuantity AND ic.inventory.id = :inventoryId")
    List<InventoryCount> findDiscrepancies(@Param("inventoryId") Long inventoryId);
}
