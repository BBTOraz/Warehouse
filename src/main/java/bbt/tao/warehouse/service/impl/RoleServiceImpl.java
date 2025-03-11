package bbt.tao.warehouse.service.impl;

import bbt.tao.warehouse.model.Permission;
import bbt.tao.warehouse.model.Role;
import bbt.tao.warehouse.model.enums.RoleType;
import bbt.tao.warehouse.repository.PermissionRepository;
import bbt.tao.warehouse.repository.RoleRepository;
import bbt.tao.warehouse.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> findRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public Optional<Role> findRoleByName(RoleType name) {
        return roleRepository.findByRole(name);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public List<Permission> findPermissionsByRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));
        return role.getPermissions();
    }

    @Override
    public void assignPermissionToRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found with id: " + permissionId));

        role.getPermissions().add(permission);
        roleRepository.save(role);
    }

    @Override
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found with id: " + permissionId));

        role.getPermissions().remove(permission);
        roleRepository.save(role);
    }
}
