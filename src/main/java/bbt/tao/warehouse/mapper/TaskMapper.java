package bbt.tao.warehouse.mapper;

import bbt.tao.warehouse.dto.task.TaskDTO;
import bbt.tao.warehouse.model.Task;
import bbt.tao.warehouse.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

    @Mapper(componentModel = "spring", uses = {UserMapper.class})
    public interface TaskMapper {

        @Mapping(target = "assignedUser", source = "assignedUser", qualifiedByName = "userToUserId")
        TaskDTO toDTO(Task task);

        @Mapping(target = "assignedUser", source = "assignedUser", qualifiedByName = "userIdToUser")
        Task toEntity(TaskDTO taskDTO);

        @Mapping(target = "assignedUser", source = "assignedUser", qualifiedByName = "idToUser")
        void updateEntityFromDTO(TaskDTO taskDTO, @MappingTarget Task task);

        @Mapping(target = "assignedUserDetails", source = "assignedUser")
        @Mapping(target = "assignedUser", source = "assignedUser", qualifiedByName = "userToUserId")
        TaskDTO toDetailsDTO(Task task);

        @Named("userToUserId")
        default Long map(User user) {
            return user == null ? null : user.getId();
        }

        @Named("idToUser")
        default User idToUser(Long id) {
            if (id == null) {
                return null;
            }
            User user = new User();
            user.setId(id);
            return user;
        }

        @Named("userIdToUser")
        default User map(Long userId) {
            if(userId == null) return null;
            User user = new User();
            user.setId(userId);
            return user;
        }
    }
