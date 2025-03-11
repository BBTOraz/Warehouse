package bbt.tao.warehouse.service;

import bbt.tao.warehouse.model.Permission;
import bbt.tao.warehouse.model.enums.PermissionType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface PermissionService {

    List<Permission> findAllPermissions();

    Optional<Permission> findPermissionById(Long id);

    Optional<Permission> findPermissionByName(PermissionType name);

    Permission savePermission(Permission permission);

    void deletePermission(Long id);
}
