package bbt.tao.warehouse.repository;


import bbt.tao.warehouse.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    List<InventoryItem> findByProduct_Id(Long productId);

    List<InventoryItem> findByLocation_Id(Long locationId);

    List<InventoryItem> findByLocation_Warehouse_Id(Long warehouseId);

    Optional<InventoryItem> findByProduct_IdAndLocation_IdAndBatchNumber(Long productId, Long locationId, String batchNumber);

    @Query("SELECT i FROM InventoryItem i WHERE i.expirationDate <= :date")
    List<InventoryItem> findExpiredItems(@Param("date") LocalDate date);

    @Query("SELECT i FROM InventoryItem i JOIN i.product p WHERE i.quantity <= p.minStockLevel")
    List<InventoryItem> findItemsBelowMinimumLevel();
}
