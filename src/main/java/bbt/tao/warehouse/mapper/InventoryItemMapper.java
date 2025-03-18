package bbt.tao.warehouse.mapper;

import bbt.tao.warehouse.dto.inventory.InventoryItemDTO;
import bbt.tao.warehouse.model.InventoryItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {InventoryMapper.class, ProductMapper.class})
public interface InventoryItemMapper {

    @Mapping(target = "location", source = "location")
    @Mapping(target = "product", source = "product")
    InventoryItemDTO toDTO(InventoryItem inventoryItem);

    @Mapping(target = "location", source = "location")
    @Mapping(target = "product", source = "product")
    InventoryItem toEntity(InventoryItemDTO inventoryItemDTO);

    List<InventoryItemDTO> toDTOList(List<InventoryItem> inventoryItems);

    List<InventoryItem> toEntityList(List<InventoryItemDTO> inventoryItemDTOs);
}