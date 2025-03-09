package bbt.tao.warehouse.service.impl;

import bbt.tao.warehouse.model.InventoryCount;
import bbt.tao.warehouse.model.Inventory;
import bbt.tao.warehouse.model.enums.InventoryStatus;
import bbt.tao.warehouse.repository.InventoryCountRepository;
import bbt.tao.warehouse.repository.InventoryRepository;
import bbt.tao.warehouse.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryCountRepository inventoryCountRepository;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository, InventoryCountRepository inventoryCountRepository) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryCountRepository = inventoryCountRepository;
    }

    @Override
    public List<Inventory> findAllInventories() {
        return inventoryRepository.findAll();
    }

    @Override
    public Optional<Inventory> findInventoryById(Long id) {
        return inventoryRepository.findById(id);
    }

    @Override
    public List<Inventory> findInventoriesByWarehouse(Long warehouseId) {
        return inventoryRepository.findByWarehouse_Id(warehouseId);
    }


    @Override
    public List<Inventory> findInventoriesByStatus(InventoryStatus status) {
        return inventoryRepository.findByStatus(status);
    }

    @Override
    public List<Inventory> findInventoriesByUser(Long userId) {
        return inventoryRepository.findByCreatedBy_Id(userId);
    }

    @Override
    public List<Inventory> findInventoriesByNumber(String inventoryNumber) {
        return inventoryRepository.findByInventoryNumber(inventoryNumber);
    }

    @Override
    public List<Inventory> findInventoriesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return inventoryRepository.findByStartDateBetween(startDate, endDate);
    }

    @Override
    public Inventory createInventory(Inventory inventory) {
        inventory.setStatus(InventoryStatus.PLANNED);
        inventory.setStartDate(LocalDateTime.now());
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory updateInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory changeStatus(Long id, InventoryStatus status) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found with id: " + id));

        inventory.setStatus(status);

        if (status == InventoryStatus.COMPLETED) {
            inventory.setEndDate(LocalDateTime.now());
        }

        return inventoryRepository.save(inventory);
    }

    @Override
    public List<InventoryCount> findCountsByInventory(Long inventoryId) {
        return inventoryCountRepository.findByInventory_Id(inventoryId);
    }

    @Override
    public List<InventoryCount> findDiscrepancies(Long inventoryId) {
        return inventoryCountRepository.findDiscrepancies(inventoryId);
    }

    @Override
    public InventoryCount saveCount(InventoryCount count) {
        count.setCountDate(LocalDateTime.now());
        return inventoryCountRepository.save(count);
    }

    @Override
    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }
}
