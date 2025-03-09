package bbt.tao.warehouse.service;

import bbt.tao.warehouse.model.Inventory;
import bbt.tao.warehouse.model.InventoryCount;
import bbt.tao.warehouse.model.enums.InventoryStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public interface InventoryService {

    List<Inventory> findAllInventories();

    Optional<Inventory> findInventoryById(Long id);

    List<Inventory> findInventoriesByWarehouse(Long warehouseId);

    List<Inventory> findInventoriesByStatus(InventoryStatus status);

    List<Inventory> findInventoriesByUser(Long userId);

    List<Inventory> findInventoriesByNumber(String inventoryNumber);

    List<Inventory> findInventoriesByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Inventory createInventory(Inventory inventory);

    Inventory updateInventory(Inventory inventory);

    Inventory changeStatus(Long id, InventoryStatus status);

    List<InventoryCount> findCountsByInventory(Long inventoryId);

    List<InventoryCount> findDiscrepancies(Long inventoryId);

    InventoryCount saveCount(InventoryCount count);

    void deleteInventory(Long id);
}
