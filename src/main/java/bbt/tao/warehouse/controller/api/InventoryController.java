package bbt.tao.warehouse.controller.api;

import bbt.tao.warehouse.dto.inventory.InventoryCountDTO;
import bbt.tao.warehouse.dto.inventory.InventoryDTO;
import bbt.tao.warehouse.service.InventoryService;
import bbt.tao.warehouse.model.enums.InventoryStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<InventoryDTO> getAllInventories() {
        return inventoryService.findAllInventories();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<InventoryDTO> getInventoryById(@PathVariable Long id) {
        return inventoryService.findInventoryById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Inventory not found with id: " + id));
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<InventoryDTO> createInventory(@RequestBody InventoryDTO inventoryDTO) {
        if (inventoryDTO == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Inventory data cannot be empty");
        }

        InventoryDTO createdInventory = inventoryService.createInventory(inventoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInventory);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<InventoryDTO> updateInventory(
            @PathVariable Long id,
            @RequestBody InventoryDTO inventoryDTO) {

        if (inventoryDTO == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Inventory data cannot be empty");
        }

        inventoryService.findInventoryById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cannot update. Inventory not found with id: " + id));

        inventoryDTO.setId(id);
        InventoryDTO updatedInventory = inventoryService.updateInventory(inventoryDTO);
        return ResponseEntity.ok(updatedInventory);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<InventoryDTO> changeInventoryStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        try {
            InventoryStatus inventoryStatus = InventoryStatus.valueOf(status.toUpperCase());
            InventoryDTO updatedInventory = inventoryService.changeStatus(id, inventoryStatus);
            return ResponseEntity.ok(updatedInventory);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid status value: " + status);
        }
    }

    @GetMapping("/{id}/counts")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<InventoryCountDTO> getInventoryCounts(@PathVariable Long id) {
        if (inventoryService.findInventoryById(id).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Inventory not found with id: " + id);
        }
        return inventoryService.findCountsByInventory(id);
    }

    @GetMapping("/{id}/discrepancies")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<InventoryCountDTO> getInventoryDiscrepancies(@PathVariable Long id) {
        if (inventoryService.findInventoryById(id).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Inventory not found with id: " + id);
        }
        return inventoryService.findDiscrepancies(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.findInventoryById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cannot delete. Inventory not found with id: " + id));

        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}