package bbt.tao.warehouse.service.impl;

    import bbt.tao.warehouse.dto.location.LocationDTO;
    import bbt.tao.warehouse.dto.warehouse.WarehouseDTO;
    import bbt.tao.warehouse.mapper.LocationMapper;
    import bbt.tao.warehouse.mapper.WarehouseMapper;
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
        private final WarehouseMapper warehouseMapper;
        private final LocationMapper locationMapper;

        @Autowired
        public WarehouseServiceImpl(
                WarehouseRepository warehouseRepository,
                LocationRepository locationRepository,
                WarehouseMapper warehouseMapper,
                LocationMapper locationMapper) {
            this.warehouseRepository = warehouseRepository;
            this.locationRepository = locationRepository;
            this.warehouseMapper = warehouseMapper;
            this.locationMapper = locationMapper;
        }

        @Override
        public List<WarehouseDTO> findAllWarehouses() {
            List<Warehouse> warehouses = warehouseRepository.findAll();
            return warehouseMapper.toDTOList(warehouses);
        }

        @Override
        public List<WarehouseDTO> findAllActiveWarehouses() {
            List<Warehouse> warehouses = warehouseRepository.findByIsActiveTrue();
            return warehouseMapper.toDTOList(warehouses);
        }

        @Override
        public Optional<WarehouseDTO> findWarehouseById(Long id) {
            return warehouseRepository.findById(id)
                    .map(warehouseMapper::toDTO);
        }

        @Override
        public List<WarehouseDTO> findWarehousesByName(String name) {
            List<Warehouse> warehouses = warehouseRepository.findByNameContainingIgnoreCase(name);
            return warehouseMapper.toDTOList(warehouses);
        }

        @Override
        public WarehouseDTO saveWarehouse(WarehouseDTO warehouseDTO) {
            Warehouse warehouse;

            if (warehouseDTO.getId() != null) {
                // Update existing warehouse
                Optional<Warehouse> existingWarehouse = warehouseRepository.findById(warehouseDTO.getId());
                if (existingWarehouse.isPresent()) {
                    warehouse = existingWarehouse.get();
                    warehouseMapper.updateEntityFromDTO(warehouseDTO, warehouse);
                } else {
                    warehouse = warehouseMapper.toEntity(warehouseDTO);
                }
            } else {
                // Create new warehouse
                warehouse = warehouseMapper.toEntity(warehouseDTO);
            }

            Warehouse savedWarehouse = warehouseRepository.save(warehouse);
            return warehouseMapper.toDTO(savedWarehouse);
        }

        @Override
        public void deleteWarehouse(Long id) {
            warehouseRepository.deleteById(id);
        }

        @Override
        public List<LocationDTO> findLocationsByWarehouse(Long warehouseId) {
            List<Location> locations = locationRepository.findByWarehouse_Id(warehouseId);
            return locationMapper.toDTOList(locations);
        }

        @Override
        public Optional<LocationDTO> findLocationById(Long id) {
            return locationRepository.findById(id)
                    .map(locationMapper::toDTO);
        }

        @Override
        public Optional<LocationDTO> findLocationByCode(String code, Long warehouseId) {
            return locationRepository.findByCodeAndWarehouse_Id(code, warehouseId)
                    .map(locationMapper::toDTO);
        }

        public List<LocationDTO> findAllLocations() {
            List<Location> locations = locationRepository.findAll();
            return locationMapper.toDTOList(locations);
        }

        @Override
        public List<LocationDTO> findSuitableLocations(Double weight, Double volume, Long warehouseId) {
            List<Location> locations = locationRepository.findSuitableLocations(weight, volume, warehouseId);
            return locationMapper.toDTOList(locations);
        }

        @Override
        public LocationDTO saveLocation(LocationDTO locationDTO) {
            Location location;

            if (locationDTO.getId() != null) {
                // Update existing location
                Optional<Location> existingLocation = locationRepository.findById(locationDTO.getId());
                if (existingLocation.isPresent()) {
                    location = existingLocation.get();
                    locationMapper.updateEntityFromDTO(locationDTO, location);
                } else {
                    location = locationMapper.toEntity(locationDTO);
                }
            } else {
                // Create new location
                location = locationMapper.toEntity(locationDTO);
            }

            Location savedLocation = locationRepository.save(location);
            return locationMapper.toDTO(savedLocation);
        }

        @Override
        public void deleteLocation(Long id) {
            locationRepository.deleteById(id);
        }
    }