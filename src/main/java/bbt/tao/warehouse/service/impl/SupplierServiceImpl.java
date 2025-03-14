package bbt.tao.warehouse.service.impl;

import bbt.tao.warehouse.dto.supplier.SupplierDTO;
import bbt.tao.warehouse.mapper.SupplierMapper;
import bbt.tao.warehouse.model.Supplier;
import bbt.tao.warehouse.repository.SupplierRepository;
import bbt.tao.warehouse.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Autowired
    public SupplierServiceImpl(SupplierRepository supplierRepository, SupplierMapper supplierMapper) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
    }

    @Override
    public List<SupplierDTO> findAllSuppliers() {
        List<Supplier> suppliers = supplierRepository.findAll();
        return supplierMapper.toDTOList(suppliers);
    }

    @Override
    public List<SupplierDTO> findAllActiveSuppliers() {
        List<Supplier> suppliers = supplierRepository.findByIsActiveTrue();
        return supplierMapper.toDTOList(suppliers);
    }

    @Override
    public Optional<SupplierDTO> findSupplierById(Long id) {
        return supplierRepository.findById(id)
                .map(supplierMapper::toDTO);
    }

    @Override
    public List<SupplierDTO> findSuppliersByName(String name) {
        List<Supplier> suppliers = supplierRepository.findByNameContainingIgnoreCase(name);
        return supplierMapper.toDTOList(suppliers);
    }

    @Override
    public Optional<SupplierDTO> findSupplierByTaxId(String taxId) {
        return supplierRepository.findByTaxId(taxId)
                .map(supplierMapper::toDTO);
    }

@Override
public SupplierDTO saveSupplier(SupplierDTO supplierDTO) {
    if (supplierDTO == null) {
        throw new IllegalArgumentException("Supplier data cannot be null");
    }

    Supplier supplier;

    if (supplierDTO.getId() != null) {
        Optional<Supplier> existingSupplier = supplierRepository.findById(supplierDTO.getId());
        if (existingSupplier.isPresent()) {
            supplier = existingSupplier.get();
            supplierMapper.updateEntityFromDTO(supplierDTO, supplier);
        } else {
            supplier = supplierMapper.toEntity(supplierDTO);
        }
    } else {
        supplier = supplierMapper.toEntity(supplierDTO);

        if (supplier.getIsActive() == null) {
            supplier.setIsActive(true);
        }
    }

    Supplier savedSupplier = supplierRepository.save(supplier);
    return supplierMapper.toDTO(savedSupplier);
}
    @Override
    public void deleteSupplier(Long id) {
        supplierRepository.deleteById(id);
    }
}