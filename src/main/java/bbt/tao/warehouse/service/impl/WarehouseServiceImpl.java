package bbt.tao.warehouse.service.impl;

import bbt.tao.warehouse.model.Location;
import bbt.tao.warehouse.model.Warehouse;
import bbt.tao.warehouse.repository.LocationRepository;
import bbt.tao.warehouse.repository.WarehouseRepository;
import bbt.tao.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, LocationRepository locationRepository) {
        this.warehouseRepository = warehouseRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public List<Warehouse> findAllWarehouses() {
        return warehouseRepository.findAll();
    }

    @Override
    public List<Warehouse> findAllActiveWarehouses() {
        return warehouseRepository.findByIsActiveTrue();
    }

    @Override
    public Optional<Warehouse> findWarehouseById(Long id) {
        return warehouseRepository.findById(id);
    }

    @Override
    public List<Warehouse> findWarehousesByName(String name) {
        return warehouseRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public Warehouse saveWarehouse(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }

    @Override
    public void deleteWarehouse(Long id) {
        warehouseRepository.deleteById(id);
    }

    @Override
    public List<Location> findLocationsByWarehouse(Long warehouseId) {
        return locationRepository.findByWarehouse_Id(warehouseId);
    }

    @Override
    public Optional<Location> findLocationById(Long id) {
        return locationRepository.findById(id);
    }

    @Override
    public Optional<Location> findLocationByCode(String code, Long warehouseId) {
        return locationRepository.findByCodeAndWarehouse_Id(code, warehouseId);
    }

    @Override
    public List<Location> findSuitableLocations(Double weight, Double volume, Long warehouseId) {
        return locationRepository.findSuitableLocations(weight, volume, warehouseId);
    }

    @Override
    public Location saveLocation(Location location) {
        return locationRepository.save(location);
    }

    @Override
    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
    }
}
