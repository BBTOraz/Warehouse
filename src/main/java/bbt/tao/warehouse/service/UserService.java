package bbt.tao.warehouse.service;

import bbt.tao.warehouse.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    List<User> findAllUsers();

    List<User> findAllActiveUsers();

    Optional<User> findUserById(Long id);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    List<User> findUsersByRole(String roleName);

    User saveUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);

    void changePassword(Long userId, String newPassword);

    void assignRole(Long userId, String roleName);

    void removeRole(Long userId, String roleName);

    boolean checkIfValidOldPassword(User user, String oldPassword);

    void recordLogin(String username);
}

