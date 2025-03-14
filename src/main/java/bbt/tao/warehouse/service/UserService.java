package bbt.tao.warehouse.service;

import bbt.tao.warehouse.dto.user.UserDTO;
import bbt.tao.warehouse.model.enums.RoleType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    List<UserDTO> findAllUsers();

    List<UserDTO> findAllActiveUsers();

    Optional<UserDTO> findUserById(Long id);

    Optional<UserDTO> findUserByUsername(String username);

    Optional<UserDTO> findUserByEmail(String email);

    List<UserDTO> findUsersByRole(RoleType roleName);

    UserDTO saveUser(UserDTO user);

    UserDTO updateUser(UserDTO user);

    void deleteUser(Long id);

    void changePassword(Long userId, String newPassword);

    void assignRole(Long userId, RoleType roleName);

    void removeRole(Long userId, RoleType roleName);

    boolean checkIfValidOldPassword(UserDTO user, String oldPassword);

    void recordLogin(String username);
}

