package bbt.tao.warehouse.controller;

import bbt.tao.warehouse.model.Warehouse;
import bbt.tao.warehouse.service.WarehouseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Warehouse> getAllWarehouses() {
        return warehouseService.findAllWarehouses();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Optional<Warehouse> getWarehouseById(@PathVariable Long id) {
        return warehouseService.findWarehouseById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Warehouse createWarehouse(@RequestBody Warehouse warehouse) {
        return warehouseService.saveWarehouse(warehouse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Warehouse updateWarehouse(@PathVariable Long id, @RequestBody Warehouse warehouse) {
        warehouse.setId(id);
        return warehouseService.saveWarehouse(warehouse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
    }

    // Эндпоинт для получения местоположений конкретного склада
    @GetMapping("/{id}/locations")
    @PreAuthorize("isAuthenticated()")
    public List<?> getLocationsByWarehouse(@PathVariable Long id) {
        return warehouseService.findLocationsByWarehouse(id);
    }
}
