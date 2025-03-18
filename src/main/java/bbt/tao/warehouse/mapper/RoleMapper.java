package bbt.tao.warehouse.mapper;



import bbt.tao.warehouse.dto.role.RoleDTO;
import bbt.tao.warehouse.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {bbt.tao.warehouse.mapper.PermissionMapper.class})
public interface RoleMapper {
    RoleDTO toDTO(Role role);
    Role toEntity(RoleDTO dto);
}

