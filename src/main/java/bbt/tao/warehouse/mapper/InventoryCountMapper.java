package bbt.tao.warehouse.mapper;

import bbt.tao.warehouse.dto.inventory.InventoryCountDTO;
import bbt.tao.warehouse.dto.inventory.InventoryCountSummaryDTO;
import bbt.tao.warehouse.model.InventoryCount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        bbt.tao.warehouse.mapper.InventoryMapper.class,
        bbt.tao.warehouse.mapper.ProductMapper.class,
        bbt.tao.warehouse.mapper.LocationMapper.class,
        bbt.tao.warehouse.mapper.UserMapper.class
})
public interface InventoryCountMapper {

    @Mapping(target = "inventory", source = "inventory")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "countedBy", source = "countedBy")
    InventoryCountDTO toDTO(InventoryCount count);

    InventoryCount toEntity(InventoryCountDTO dto);

    @Mapping(target = "inventoryId", source = "inventory.id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "locationId", source = "location.id")
    InventoryCountSummaryDTO toSummaryDTO(InventoryCount count);
}
