package bbt.tao.warehouse.mapper;

import bbt.tao.warehouse.dto.permission.PermissionDTO;
import bbt.tao.warehouse.model.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    @Mapping(target = "permission", source = "name")
    PermissionDTO toDTO(Permission permission);

    @Mapping(target = "name", source = "permission")
    Permission toEntity(PermissionDTO dto);
}
