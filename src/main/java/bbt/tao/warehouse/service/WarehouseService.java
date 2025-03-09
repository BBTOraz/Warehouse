package bbt.tao.warehouse.service;

import bbt.tao.warehouse.model.Location;
import bbt.tao.warehouse.model.Warehouse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface WarehouseService {

    List<Warehouse> findAllWarehouses();

    List<Warehouse> findAllActiveWarehouses();

    Optional<Warehouse> findWarehouseById(Long id);

    List<Warehouse> findWarehousesByName(String name);

    Warehouse saveWarehouse(Warehouse warehouse);

    void deleteWarehouse(Long id);

    List<Location> findLocationsByWarehouse(Long warehouseId);

    Optional<Location> findLocationById(Long id);

    Optional<Location> findLocationByCode(String code, Long warehouseId);

    List<Location> findSuitableLocations(Double weight, Double volume, Long warehouseId);

    Location saveLocation(Location location);

    void deleteLocation(Long id);
}
