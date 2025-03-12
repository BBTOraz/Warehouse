package bbt.tao.warehouse.controller;

import bbt.tao.warehouse.model.User;
import bbt.tao.warehouse.model.enums.RoleType;
import bbt.tao.warehouse.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/{id}/change-password")
    @PreAuthorize("hasRole('ADMIN')")
    public void changePassword(@PathVariable Long id, @RequestParam String newPassword) {
        userService.changePassword(id, newPassword);
    }

    @PostMapping("/{id}/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    public void assignRole(@PathVariable Long id, @RequestParam RoleType role) {
        userService.assignRole(id, role);
    }

    @PostMapping("/{id}/remove-role")
    @PreAuthorize("hasRole('ADMIN')")
    public void removeRole(@PathVariable Long id, @RequestParam RoleType role) {
        userService.removeRole(id, role);
    }
}
