package bbt.tao.warehouse.mapper;


import bbt.tao.warehouse.dto.user.UserDTO;
import bbt.tao.warehouse.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {bbt.tao.warehouse.mapper.RoleMapper.class})
public interface UserMapper {
    UserDTO toDTO(User user);
    User toEntity(UserDTO dto);
    void updateEntityFromDto(UserDTO userDto, @MappingTarget User user);
}

