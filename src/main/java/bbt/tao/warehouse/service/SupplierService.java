package bbt.tao.warehouse.service;

import bbt.tao.warehouse.model.Supplier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface SupplierService {

    List<Supplier> findAllSuppliers();

    List<Supplier> findAllActiveSuppliers();

    Optional<Supplier> findSupplierById(Long id);

    List<Supplier> findSuppliersByName(String name);

    Optional<Supplier> findSupplierByTaxId(String taxId);

    Supplier saveSupplier(Supplier supplier);

    void deleteSupplier(Long id);
}
