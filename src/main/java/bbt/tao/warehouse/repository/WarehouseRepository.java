package bbt.tao.warehouse.repository;

import bbt.tao.warehouse.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    List<Warehouse> findByIsActiveTrue();

    List<Warehouse> findByNameContainingIgnoreCase(String name);
}
