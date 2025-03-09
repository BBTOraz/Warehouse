package bbt.tao.warehouse.repository;

import bbt.tao.warehouse.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    List<Supplier> findByIsActiveTrue();

    List<Supplier> findByNameContainingIgnoreCase(String name);

    Optional<Supplier> findByTaxId(String taxId);
}
