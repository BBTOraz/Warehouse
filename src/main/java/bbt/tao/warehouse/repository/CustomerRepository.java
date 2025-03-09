package bbt.tao.warehouse.repository;

import bbt.tao.warehouse.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByIsActiveTrue();

    List<Customer> findByNameContainingIgnoreCase(String name);

    Optional<Customer> findByTaxId(String taxId);
}
