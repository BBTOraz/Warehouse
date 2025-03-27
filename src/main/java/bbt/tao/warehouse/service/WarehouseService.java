package bbt.tao.warehouse.service;

import bbt.tao.warehouse.dto.location.LocationDTO;
import bbt.tao.warehouse.dto.warehouse.WarehouseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface WarehouseService {

    List<WarehouseDTO> findAllWarehouses();

    List<WarehouseDTO> findAllActiveWarehouses();

    Optional<WarehouseDTO> findWarehouseById(Long id);

    List<WarehouseDTO> findWarehousesByName(String name);

    WarehouseDTO saveWarehouse(WarehouseDTO warehouseDTO);

    List<LocationDTO> findAllLocations();

    void deleteWarehouse(Long id);

    List<LocationDTO> findLocationsByWarehouse(Long warehouseId);

    Optional<LocationDTO> findLocationById(Long id);

    Optional<LocationDTO> findLocationByCode(String code, Long warehouseId);

    List<LocationDTO> findSuitableLocations(Double weight, Double volume, Long warehouseId);

    LocationDTO saveLocation(LocationDTO locationDTO);

    void deleteLocation(Long id);
}