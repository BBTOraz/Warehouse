package bbt.tao.warehouse.controller.manager;


import bbt.tao.warehouse.dto.customer.CustomerDTO;
import bbt.tao.warehouse.dto.supplier.SupplierDTO;
import bbt.tao.warehouse.service.CustomerService;
import bbt.tao.warehouse.service.SupplierService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/partners")
@PreAuthorize("hasRole('MANAGER')")
@AllArgsConstructor
public class ManagerPartnersController {

    private final CustomerService customerService;
    private final SupplierService supplierService;

    @GetMapping
    public String getPartnersPage(Model model) {
        model.addAttribute("customers", customerService.findAllCustomers());
        model.addAttribute("suppliers", supplierService.findAllSuppliers());
        return "manager/partners/list";
    }

    @GetMapping("/customers")
    public String getCustomersPage(Model model) {
        model.addAttribute("customers", customerService.findAllCustomers());
        return "manager/partners/customers";
    }

    @GetMapping("/customers/{id}")
    public String getCustomerDetails(@PathVariable Long id, Model model) {
        Optional<CustomerDTO> customer = customerService.findCustomerById(id);
        if (customer.isPresent()) {
            model.addAttribute("customer", customer.get());
            return "manager/partners/customer-details";
        }
        return "redirect:/partners/customers";
    }

    @GetMapping("/customers/edit/{id}")
    public String editCustomerForm(@PathVariable Long id, Model model) {
        Optional<CustomerDTO> customer = customerService.findCustomerById(id);
        if (customer.isPresent()) {
            model.addAttribute("customer", customer.get());
            return "manager/partners/customer-form";
        }
        return "redirect:/partners/customers";
    }

    @PostMapping("/customers/edit/{id}")
    public String updateCustomer(@PathVariable Long id, @ModelAttribute CustomerDTO customerDTO, RedirectAttributes redirectAttributes) {
        try {
            customerDTO.setId(id);
            customerService.saveCustomer(customerDTO);
            redirectAttributes.addFlashAttribute("success", "Customer updated successfully");
            return "redirect:/partners/customers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/partners/customers/edit/" + id;
        }
    }

    @PostMapping("/customers/{id}/delete")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("success", "Customer deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/partners/customers";
    }

    @GetMapping("/customers/search")
    public String searchCustomers(@RequestParam String name, Model model) {
        model.addAttribute("customers", customerService.findCustomersByName(name));
        return "partners/customers";
    }

    // Supplier management
    @GetMapping("/suppliers")
    public String getSuppliersPage(Model model) {
        model.addAttribute("suppliers", supplierService.findAllSuppliers());
        return "manager/partners/suppliers";
    }

    @GetMapping("/suppliers/{id}")
    public String getSupplierDetails(@PathVariable Long id, Model model) {
        Optional<SupplierDTO> supplier = supplierService.findSupplierById(id);
        if (supplier.isPresent()) {
            model.addAttribute("supplier", supplier.get());
            return "manager/partners/supplier-details";
        }
        return "redirect:/partners/suppliers";
    }

    @GetMapping("/suppliers/edit/{id}")
    public String editSupplierForm(@PathVariable Long id, Model model) {
        Optional<SupplierDTO> supplier = supplierService.findSupplierById(id);
        if (supplier.isPresent()) {
            model.addAttribute("supplier", supplier.get());
            return "manager/partners/supplier-form";
        }
        return "redirect:/partners/suppliers";
    }

    @PostMapping("/suppliers/edit/{id}")
    public String updateSupplier(@PathVariable Long id, @ModelAttribute SupplierDTO supplierDTO, RedirectAttributes redirectAttributes) {
        try {
            supplierDTO.setId(id);
            supplierService.saveSupplier(supplierDTO);
            redirectAttributes.addFlashAttribute("success", "Supplier updated successfully");
            return "redirect:/partners/suppliers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/partners/suppliers/edit/" + id;
        }
    }

    @PostMapping("/suppliers/{id}/delete")
    public String deleteSupplier(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            supplierService.deleteSupplier(id);
            redirectAttributes.addFlashAttribute("success", "Supplier deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/partners/suppliers";
    }

    @GetMapping("/suppliers/search")
    public String searchSuppliers(@RequestParam String name, Model model) {
        model.addAttribute("suppliers", supplierService.findSuppliersByName(name));
        return "manager/partners/suppliers";
    }

    // REST API endpoints for AJAX calls
    @GetMapping("/api/customers")
    @ResponseBody
    public ResponseEntity<?> getActiveCustomers() {
        return ResponseEntity.ok(customerService.findAllActiveCustomers());
    }

    @GetMapping("/api/suppliers")
    @ResponseBody
    public ResponseEntity<?> getActiveSuppliers() {
        return ResponseEntity.ok(supplierService.findAllActiveSuppliers());
    }

    @GetMapping("/api/customers/{taxId}")
    @ResponseBody
    public ResponseEntity<?> getCustomerByTaxId(@PathVariable String taxId) {
        Optional<CustomerDTO> customer = customerService.findCustomerByTaxId(taxId);
        return customer.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/api/suppliers/{taxId}")
    @ResponseBody
    public ResponseEntity<?> getSupplierByTaxId(@PathVariable String taxId) {
        Optional<SupplierDTO> supplier = supplierService.findSupplierByTaxId(taxId);
        return supplier.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
