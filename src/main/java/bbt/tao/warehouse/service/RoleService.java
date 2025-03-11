package bbt.tao.warehouse.service;

import bbt.tao.warehouse.model.Permission;
import bbt.tao.warehouse.model.Role;
import bbt.tao.warehouse.model.enums.RoleType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public interface RoleService {

    List<Role> findAllRoles();

    Optional<Role> findRoleById(Long id);

    Optional<Role> findRoleByName(RoleType name);

    Role saveRole(Role role);

    void deleteRole(Long id);

    List<Permission> findPermissionsByRole(Long roleId);

    void assignPermissionToRole(Long roleId, Long permissionId);

    void removePermissionFromRole(Long roleId, Long permissionId);
}
