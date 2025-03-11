package bbt.tao.warehouse.repository;

import bbt.tao.warehouse.model.Permission;
import bbt.tao.warehouse.model.enums.PermissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByName(PermissionType name);
}
