package bbt.tao.warehouse.service;

import bbt.tao.warehouse.dto.inventory.InventoryCountDTO;
import bbt.tao.warehouse.dto.inventory.InventoryDTO;
import bbt.tao.warehouse.dto.inventory.InventorySummeryDTO;
import bbt.tao.warehouse.dto.user.UserDTO;
import bbt.tao.warehouse.model.InventoryCount;
import bbt.tao.warehouse.model.enums.InventoryStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public interface InventoryService {

    List<InventoryDTO> findAllInventories();

    Optional<InventoryDTO> findInventoryById(Long id);

    List<InventoryDTO> findInventoriesByWarehouse(Long warehouseId);

    List<InventoryDTO> findInventoriesByStatus(InventoryStatus status);

    List<InventoryDTO> findInventoriesByUser(Long userId);

    List<InventoryDTO> findInventoriesByNumber(String inventoryNumber);

    List<InventoryDTO> findInventoriesByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    InventoryDTO createInventory(InventoryDTO inventoryDTO, UserDTO createdBy);

    InventoryDTO updateInventory(InventoryDTO inventoryDTO);

    InventoryDTO changeStatus(Long id, InventoryStatus status);

    List<InventoryCountDTO> findCountsByInventory(Long inventoryId);

    List<InventoryCountDTO> findDiscrepancies(Long inventoryId);

    List<InventoryCountDTO> findInventoryCountsByUserId(Long id);

    InventoryCount saveCount(InventoryCount count);

    void deleteInventory(Long id);

    int getPendingOrdersCount();
}