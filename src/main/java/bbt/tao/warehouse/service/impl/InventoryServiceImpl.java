package bbt.tao.warehouse.service.impl;

import bbt.tao.warehouse.dto.inventory.InventoryCountDTO;
import bbt.tao.warehouse.dto.inventory.InventoryDTO;
import bbt.tao.warehouse.dto.user.UserDTO;
import bbt.tao.warehouse.mapper.InventoryCountMapper;
import bbt.tao.warehouse.mapper.InventoryMapper;
import bbt.tao.warehouse.mapper.WarehouseMapper;
import bbt.tao.warehouse.model.InventoryCount;
import bbt.tao.warehouse.model.Inventory;
import bbt.tao.warehouse.model.enums.InventoryStatus;
import bbt.tao.warehouse.repository.InventoryCountRepository;
import bbt.tao.warehouse.repository.InventoryRepository;
import bbt.tao.warehouse.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryCountRepository inventoryCountRepository;
    private final InventoryMapper inventoryMapper;
    private final InventoryCountMapper inventoryCountMapper;
    private final WarehouseMapper warehouseMapper;

    @Autowired
    public InventoryServiceImpl(
            InventoryRepository inventoryRepository,
            InventoryCountRepository inventoryCountRepository,
            InventoryMapper inventoryMapper, InventoryCountMapper inventoryCountMapper, WarehouseMapper warehouseMapper) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryCountRepository = inventoryCountRepository;
        this.inventoryMapper = inventoryMapper;
        this.inventoryCountMapper = inventoryCountMapper;
        this.warehouseMapper = warehouseMapper;
    }

    @Override
    public List<InventoryDTO> findAllInventories() {
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream()
                .map(inventoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<InventoryDTO> findInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .map(inventoryMapper::toDTO);
    }

    @Override
    public List<InventoryDTO> findInventoriesByWarehouse(Long warehouseId) {
        List<Inventory> inventories = inventoryRepository.findByWarehouse_Id(warehouseId);
        return inventories.stream()
                .map(inventoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryDTO> findInventoriesByStatus(InventoryStatus status) {
        List<Inventory> inventories = inventoryRepository.findByStatus(status);
        return inventories.stream()
                .map(inventoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryDTO> findInventoriesByUser(Long userId) {
        List<Inventory> inventories = inventoryRepository.findByCreatedBy_Id(userId);
        return inventories.stream()
                .map(inventoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryDTO> findInventoriesByNumber(String inventoryNumber) {
        List<Inventory> inventories = inventoryRepository.findByInventoryNumber(inventoryNumber);
        return inventories.stream()
                .map(inventoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryDTO> findInventoriesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Inventory> inventories = inventoryRepository.findByStartDateBetween(startDate, endDate);
        return inventories.stream()
                .map(inventoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryDTO createInventory(InventoryDTO inventoryDTO, UserDTO createdBy) {
        inventoryDTO.setCreatedBy(createdBy);
        Inventory inventory = inventoryMapper.toEntity(inventoryDTO);
        log.info(inventory.toString());
        if (inventoryDTO.getStatus() == null && inventoryDTO.getStartDate() == null) {
            inventory.setStatus(InventoryStatus.PLANNED);
            inventory.setStartDate(LocalDateTime.now());
        }
        Inventory savedInventory = inventoryRepository.save(inventory);
        return inventoryMapper.toDTO(savedInventory);
    }

    @Override
    public InventoryDTO updateInventory(InventoryDTO inventoryDTO) {
        if (inventoryDTO.getId() == null) {
            throw new IllegalArgumentException("Inventory ID cannot be null for update operation");
        }

        Inventory existingInventory = inventoryRepository.findById(inventoryDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found with id: " + inventoryDTO.getId()));


        existingInventory.setInventoryNumber(inventoryDTO.getInventoryNumber());
        existingInventory.setStatus(inventoryDTO.getStatus());
        existingInventory.setWarehouse(warehouseMapper.toEntity(inventoryDTO.getWarehouse()));
        existingInventory.setStartDate(inventoryDTO.getStartDate());
        existingInventory.setEndDate(inventoryDTO.getEndDate());
        Inventory updatedInventory = inventoryRepository.save(existingInventory);

        return inventoryMapper.toDTO(updatedInventory);
    }

    @Override
    public InventoryDTO changeStatus(Long id, InventoryStatus status) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found with id: " + id));

        inventory.setStatus(status);

        if (status == InventoryStatus.COMPLETED) {
            inventory.setEndDate(LocalDateTime.now());
        }

        Inventory updatedInventory = inventoryRepository.save(inventory);
        return inventoryMapper.toDTO(updatedInventory);
    }

    @Override
    public List<InventoryCountDTO> findCountsByInventory(Long inventoryId) {
        List<InventoryCount> counts = inventoryCountRepository.findByInventory_Id(inventoryId);

        return counts.stream()
                .map(inventoryCountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryCountDTO> findDiscrepancies(Long inventoryId) {
        List<InventoryCount> counts = inventoryCountRepository.findDiscrepancies(inventoryId);

        return counts.stream()
                .map(inventoryCountMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryCountDTO> findInventoryCountsByUserId(Long userId) {
        List<InventoryCount> counts = inventoryCountRepository.findByCountedBy_Id(userId);
        return counts.stream().map(inventoryCountMapper::toDTO)
                .collect(Collectors.toList());
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

    @Override
    public int getPendingOrdersCount() {
        List<InventoryDTO> pendingInventories = findInventoriesByStatus(InventoryStatus.IN_PROGRESS);
        return pendingInventories.size();
    }
}