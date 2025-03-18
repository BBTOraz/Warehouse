package bbt.tao.warehouse.mapper;

import bbt.tao.warehouse.dto.supplier.SupplierDTO;
import bbt.tao.warehouse.model.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    SupplierDTO toDTO(Supplier supplier);

    List<SupplierDTO> toDTOList(List<Supplier> suppliers);

    Supplier toEntity(SupplierDTO dto);

    void updateEntityFromDTO(SupplierDTO dto, @MappingTarget Supplier supplier);
}