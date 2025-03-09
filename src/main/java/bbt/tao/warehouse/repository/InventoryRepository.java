package bbt.tao.warehouse.repository;

import bbt.tao.warehouse.model.Inventory;
import bbt.tao.warehouse.model.enums.InventoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByWarehouse_Id(Long warehouseId);

    List<Inventory> findByStatus(InventoryStatus status);

    List<Inventory> findByCreatedBy_Id(Long userId);

    List<Inventory> findByInventoryNumber(String inventoryNumber);

    List<Inventory> findByStartDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
