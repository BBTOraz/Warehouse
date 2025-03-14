package bbt.tao.warehouse.controller.api;

import bbt.tao.warehouse.dto.warehouse.WarehouseDTO;
import bbt.tao.warehouse.service.WarehouseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WarehouseDTO>> getAllWarehouses() {
        List<WarehouseDTO> warehouses = warehouseService.findAllWarehouses();
        return ResponseEntity.ok(warehouses);
    }

    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WarehouseDTO>> getActiveWarehouses() {
        List<WarehouseDTO> activeWarehouses = warehouseService.findAllActiveWarehouses();
        return ResponseEntity.ok(activeWarehouses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WarehouseDTO> getWarehouseById(@PathVariable Long id) {
        return warehouseService.findWarehouseById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Warehouse not found with id: " + id));
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WarehouseDTO>> searchWarehouses(@RequestParam String name) {
        List<WarehouseDTO> warehouses = warehouseService.findWarehousesByName(name);
        return ResponseEntity.ok(warehouses);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WarehouseDTO> createWarehouse(@RequestBody WarehouseDTO warehouseDTO) {
        if (warehouseDTO == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Warehouse data cannot be empty");
        }

        // Ensure ID is null for creation
        warehouseDTO.setId(null);
        WarehouseDTO createdWarehouse = warehouseService.saveWarehouse(warehouseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWarehouse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WarehouseDTO> updateWarehouse(
            @PathVariable Long id,
            @RequestBody WarehouseDTO warehouseDTO) {

        if (warehouseDTO == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Warehouse data cannot be empty");
        }

        warehouseService.findWarehouseById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cannot update. Warehouse not found with id: " + id));

        warehouseDTO.setId(id);
        WarehouseDTO updatedWarehouse = warehouseService.saveWarehouse(warehouseDTO);
        return ResponseEntity.ok(updatedWarehouse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.findWarehouseById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cannot delete. Warehouse not found with id: " + id));

        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }
}