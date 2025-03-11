package bbt.tao.warehouse.repository;

import bbt.tao.warehouse.model.Role;
import bbt.tao.warehouse.model.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRole(RoleType name);
}

