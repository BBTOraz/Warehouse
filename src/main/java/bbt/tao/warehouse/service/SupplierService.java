package bbt.tao.warehouse.service;

import bbt.tao.warehouse.dto.supplier.SupplierDTO;

import java.util.List;
import java.util.Optional;

public interface SupplierService {

    List<SupplierDTO> findAllSuppliers();

    List<SupplierDTO> findAllActiveSuppliers();

    Optional<SupplierDTO> findSupplierById(Long id);

    List<SupplierDTO> findSuppliersByName(String name);

    Optional<SupplierDTO> findSupplierByTaxId(String taxId);

    SupplierDTO saveSupplier(SupplierDTO supplierDTO);

    void deleteSupplier(Long id);
}