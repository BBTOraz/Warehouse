package bbt.tao.warehouse.controller;

import bbt.tao.warehouse.model.Location;
import bbt.tao.warehouse.service.WarehouseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final WarehouseService warehouseService;

    public LocationController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    // Пример: получение местоположения по ID
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Optional<Location> getLocationById(@PathVariable Long id) {
        return warehouseService.findLocationById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Location createLocation(@RequestBody Location location) {
        return warehouseService.saveLocation(location);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Location updateLocation(@PathVariable Long id, @RequestBody Location location) {
        location.setId(id);
        return warehouseService.saveLocation(location);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteLocation(@PathVariable Long id) {
        warehouseService.deleteLocation(id);
    }
}
