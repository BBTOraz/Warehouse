package bbt.tao.warehouse.controller;

import bbt.tao.warehouse.model.Inventory;
import bbt.tao.warehouse.service.InventoryService;
import bbt.tao.warehouse.model.enums.InventoryStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<Inventory> getAllInventories() {
        return inventoryService.findAllInventories();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Optional<Inventory> getInventoryById(@PathVariable Long id) {
        return inventoryService.findInventoryById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public Inventory createInventory(@RequestBody Inventory inventory) {
        return inventoryService.createInventory(inventory);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('MANAGER')")
    public Inventory changeInventoryStatus(@PathVariable Long id, @RequestParam String status) {
        return inventoryService.changeStatus(id, InventoryStatus.valueOf(status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
    }
}
