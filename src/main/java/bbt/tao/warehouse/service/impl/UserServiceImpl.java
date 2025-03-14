package bbt.tao.warehouse.service.impl;

import bbt.tao.warehouse.dto.user.UserDTO;
import bbt.tao.warehouse.mapper.UserMapper;
import bbt.tao.warehouse.model.Role;
import bbt.tao.warehouse.model.User;
import bbt.tao.warehouse.model.enums.RoleType;
import bbt.tao.warehouse.repository.RoleRepository;
import bbt.tao.warehouse.repository.UserRepository;
import bbt.tao.warehouse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public List<UserDTO> findAllUsers() {

        List<User> users = userRepository.findAll();

        return users.stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> findAllActiveUsers() {
        List<User> users = userRepository.findByIsActiveTrue();
        return users.stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> findUserById(Long id) {
        Optional<User> user =  userRepository.findById(id);
        return user.map(userMapper::toDTO);
    }

    @Override
    public Optional<UserDTO> findUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(userMapper::toDTO);
    }

    @Override
    public Optional<UserDTO> findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(userMapper::toDTO);
    }

    @Override
    public List<UserDTO> findUsersByRole(RoleType roleName) {
        List<User> users = userRepository.findByRole(roleName);
        return users.stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO saveUser(UserDTO user) {
        if (user.getId() == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        User newUser = userMapper.toEntity(user);
        userRepository.save(newUser);
        return userMapper.toDTO(newUser);
    }

    @Override
    public UserDTO updateUser(UserDTO userDto) {
        var user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userDto.getId()));

        userMapper.updateEntityFromDto(userDto, user);

        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


    @Override
    public void changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void assignRole(Long userId, RoleType roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Role role = roleRepository.findByRole(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));

        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public void removeRole(Long userId, RoleType roleName) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Role role = roleRepository.findByRole(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));

        user.getRoles().remove(role);
        userRepository.save(user);
    }

    @Override
    public boolean checkIfValidOldPassword(UserDTO user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public void recordLogin(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }
}