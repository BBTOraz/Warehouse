package bbt.tao.warehouse.mapper;

import bbt.tao.warehouse.dto.inventory.InventoryTransactionDTO;
import bbt.tao.warehouse.dto.inventory.InventoryTransactionSummaryDTO;
import bbt.tao.warehouse.model.InventoryTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        bbt.tao.warehouse.mapper.ProductMapper.class,
        bbt.tao.warehouse.mapper.LocationMapper.class,
        bbt.tao.warehouse.mapper.SupplierMapper.class,
        bbt.tao.warehouse.mapper.CustomerMapper.class,
        bbt.tao.warehouse.mapper.UserMapper.class
})
public interface InventoryTransactionMapper {

    @Mapping(target = "product", source = "product")
    @Mapping(target = "sourceLocation", source = "sourceLocation")
    @Mapping(target = "destinationLocation", source = "destinationLocation")
    @Mapping(target = "supplier", source = "supplier")
    @Mapping(target = "customer", source = "customer")
    @Mapping(target = "user", source = "user")
    InventoryTransactionDTO toDTO(InventoryTransaction transaction);

    InventoryTransaction toEntity(InventoryTransactionDTO dto);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "transactionType", source = "transactionType")
    @Mapping(target = "transactionDate", source = "transactionDate")
    InventoryTransactionSummaryDTO toSummaryDTO(InventoryTransaction transaction);
}