package bbt.tao.warehouse.controller.api;

import bbt.tao.warehouse.dto.location.LocationDTO;
import bbt.tao.warehouse.service.WarehouseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final WarehouseService warehouseService;

    public LocationController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable Long id) {
        return warehouseService.findLocationById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Location not found with id: " + id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<LocationDTO> createLocation(@RequestBody LocationDTO locationDTO) {
        if (locationDTO == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Location data cannot be empty");
        }

        LocationDTO createdLocation = warehouseService.saveLocation(locationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLocation);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<LocationDTO> updateLocation(
            @PathVariable Long id,
            @RequestBody LocationDTO locationDTO) {

        if (locationDTO == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Location data cannot be empty");
        }

        warehouseService.findLocationById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cannot update. Location not found with id: " + id));

        locationDTO.setId(id);
        LocationDTO updatedLocation = warehouseService.saveLocation(locationDTO);
        return ResponseEntity.ok(updatedLocation);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        warehouseService.findLocationById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cannot delete. Location not found with id: " + id));

        warehouseService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}

