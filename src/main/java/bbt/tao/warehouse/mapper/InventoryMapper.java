package bbt.tao.warehouse.mapper;

import bbt.tao.warehouse.dto.inventory.InventoryDTO;
import bbt.tao.warehouse.dto.inventory.InventorySummeryDTO;
import bbt.tao.warehouse.model.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {bbt.tao.warehouse.mapper.WarehouseMapper.class,
        bbt.tao.warehouse.mapper.UserMapper.class})
public interface InventoryMapper {
    InventoryDTO toDTO(Inventory inventory);

    List<InventoryDTO> toDTOList(List<Inventory> inventories);

    @Mapping(target = "warehouse", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    Inventory toEntity(InventoryDTO dto);

    void updateEntityFromDTO(InventoryDTO dto, @MappingTarget Inventory inventory);

    InventorySummeryDTO toSummeryDTO(Inventory inventory);

    List<InventorySummeryDTO> toSummeryDTOList(List<Inventory> inventories);
}
