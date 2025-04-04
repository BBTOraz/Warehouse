package bbt.tao.warehouse.repository;

import bbt.tao.warehouse.model.User;
import bbt.tao.warehouse.model.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByIsActiveTrue();

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.role = :roleName")
    List<User> findByRole(@Param("roleName") RoleType roleName);

    Optional<User> findFirstByUsername(String username);
}
