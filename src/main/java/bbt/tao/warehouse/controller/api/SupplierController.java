package bbt.tao.warehouse.controller.api;

import bbt.tao.warehouse.dto.supplier.SupplierDTO;
import bbt.tao.warehouse.service.SupplierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers() {
        List<SupplierDTO> suppliers = supplierService.findAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SupplierDTO>> getActiveSuppliers() {
        List<SupplierDTO> suppliers = supplierService.findAllActiveSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable Long id) {
        return supplierService.findSupplierById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Supplier not found with id: " + id));
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SupplierDTO>> searchSuppliers(@RequestParam String name) {
        List<SupplierDTO> suppliers = supplierService.findSuppliersByName(name);
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/tax-id/{taxId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SupplierDTO> getSupplierByTaxId(@PathVariable String taxId) {
        return supplierService.findSupplierByTaxId(taxId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Supplier not found with tax ID: " + taxId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<SupplierDTO> createSupplier(@RequestBody SupplierDTO supplierDTO) {
        if (supplierDTO == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Supplier data cannot be empty");
        }

        // Ensure ID is null for creation
        supplierDTO.setId(null);
        SupplierDTO createdSupplier = supplierService.saveSupplier(supplierDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSupplier);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<SupplierDTO> updateSupplier(
            @PathVariable Long id,
            @RequestBody SupplierDTO supplierDTO) {

        if (supplierDTO == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Supplier data cannot be empty");
        }

        // Check if supplier exists
        supplierService.findSupplierById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cannot update. Supplier not found with id: " + id));

        supplierDTO.setId(id);
        SupplierDTO updatedSupplier = supplierService.saveSupplier(supplierDTO);
        return ResponseEntity.ok(updatedSupplier);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        // Check if supplier exists
        supplierService.findSupplierById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cannot delete. Supplier not found with id: " + id));

        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}