package bbt.tao.warehouse.controller.api;

import bbt.tao.warehouse.dto.customer.CustomerDTO;
import bbt.tao.warehouse.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<CustomerDTO> getAllCustomers() {
        return customerService.findAllCustomers();
    }

    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public List<CustomerDTO> getActiveCustomers() {
        return customerService.findAllActiveCustomers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        return customerService.findCustomerById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Customer not found with id: " + id));
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public List<CustomerDTO> searchCustomers(@RequestParam String name) {
        return customerService.findCustomersByName(name);
    }

    @GetMapping("/tax/{taxId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CustomerDTO> getCustomerByTaxId(@PathVariable String taxId) {
        return customerService.findCustomerByTaxId(taxId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Customer not found with tax ID: " + taxId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO) {
        if (customerDTO == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Customer data cannot be empty");
        }

        CustomerDTO createdCustomer = customerService.saveCustomer(customerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerDTO customerDTO) {

        if (customerDTO == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Customer data cannot be empty");
        }

        customerService.findCustomerById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cannot update. Customer not found with id: " + id));

        customerDTO.setId(id);
        CustomerDTO updatedCustomer = customerService.saveCustomer(customerDTO);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.findCustomerById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cannot delete. Customer not found with id: " + id));

        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}