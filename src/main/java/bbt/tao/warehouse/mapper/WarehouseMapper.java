package bbt.tao.warehouse.mapper;

import bbt.tao.warehouse.dto.warehouse.WarehouseDTO;
import bbt.tao.warehouse.model.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    WarehouseDTO toDTO(Warehouse warehouse);

    List<WarehouseDTO> toDTOList(List<Warehouse> warehouses);

    Warehouse toEntity(WarehouseDTO dto);

    void updateEntityFromDTO(WarehouseDTO dto, @MappingTarget Warehouse warehouse);
}