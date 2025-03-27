package bbt.tao.warehouse.controller.manager;

import bbt.tao.warehouse.dto.inventory.InventoryCountDTO;
import bbt.tao.warehouse.dto.inventory.InventoryDTO;
import bbt.tao.warehouse.dto.warehouse.WarehouseDTO;
import bbt.tao.warehouse.mapper.InventoryCountMapper;
import bbt.tao.warehouse.model.enums.InventoryStatus;
import bbt.tao.warehouse.repository.LocationRepository;
import bbt.tao.warehouse.security.CustomUserDetails;
import bbt.tao.warehouse.security.service.CustomUserDetailsServiceImpl;
import bbt.tao.warehouse.service.InventoryService;
import bbt.tao.warehouse.service.WarehouseService;
import bbt.tao.warehouse.service.impl.CustomerServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Marker;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/inventory")
@PreAuthorize("hasRole('MANAGER')")
@AllArgsConstructor
public class ManagerInventoryController {

    private final InventoryService inventoryService;
    private final InventoryCountMapper inventoryCountMapper;
    private final WarehouseService warehouseService;
    private final LocationRepository locationRepository;

    @GetMapping
    public String getInventoryPage(Model model) {
        model.addAttribute("inventories", inventoryService.findAllInventories());
        model.addAttribute("pendingCount", inventoryService.getPendingOrdersCount());
        return "manager/inventory/list";
    }

    @GetMapping("/{id}")
    public String getInventoryDetails(@PathVariable Long id, Model model) {
        Optional<InventoryDTO> inventory = inventoryService.findInventoryById(id);
        if (inventory.isPresent()) {
            model.addAttribute("inventory", inventory.get());
            model.addAttribute("counts", inventoryService.findCountsByInventory(id));
            model.addAttribute("discrepancies", inventoryService.findDiscrepancies(id));
            model.addAttribute("locations", locationRepository.findAll());
            return "manager/inventory/details";
        }
        return "redirect:/inventory";
    }

    @GetMapping("/create")
    public String createInventoryForm(Model model) {
        model.addAttribute("inventory", new InventoryDTO());
        model.addAttribute("warehouses", warehouseService.findAllActiveWarehouses());
        return "manager/inventory/form";
    }

    @PostMapping("/create")
    public String createInventory(@ModelAttribute InventoryDTO inventoryDTO, Authentication authentication) {
        log.info("{} {}", inventoryDTO.toString(), ((CustomUserDetails) authentication.getPrincipal()).getUser());
        inventoryService.createInventory(inventoryDTO, ((CustomUserDetails) authentication.getPrincipal()).getUser());

        return "redirect:/inventory";
    }

    @GetMapping("/edit/{id}")
    public String editInventoryForm(@PathVariable Long id, Model model) {
        Optional<InventoryDTO> inventory = inventoryService.findInventoryById(id);
        List<WarehouseDTO> warehouseDTOs = warehouseService.findAllActiveWarehouses();
        if (inventory.isPresent()) {
            model.addAttribute("warehouses", warehouseDTOs);
            model.addAttribute("inventory", inventory.get());
            return "manager/inventory/form";
        }
        return "redirect:/inventory";
    }

    @PostMapping("/edit/{id}")
    public String updateInventory(@PathVariable Long id, @ModelAttribute InventoryDTO inventoryDTO) {
        inventoryDTO.setId(id);
        log.info(inventoryDTO.toString());
        inventoryService.updateInventory(inventoryDTO);
        return "redirect:/inventory";
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id, @RequestParam InventoryStatus status) {
        inventoryService.changeStatus(id, status);
        return "redirect:/inventory/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return "redirect:/inventory";
    }

    @GetMapping("/search")
    public String searchInventory(
            @RequestParam(required = false) String inventoryNumber,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) InventoryStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Model model
    ) {
        List<InventoryDTO> results;

        if (inventoryNumber != null && !inventoryNumber.isEmpty()) {
            results = inventoryService.findInventoriesByNumber(inventoryNumber);
        } else if (warehouseId != null) {
            results = inventoryService.findInventoriesByWarehouse(warehouseId);
        } else if (status != null) {
            results = inventoryService.findInventoriesByStatus(status);
        } else if (startDate != null && endDate != null) {
            results = inventoryService.findInventoriesByDateRange(startDate, endDate);
        } else {
            results = inventoryService.findAllInventories();
        }

        model.addAttribute("inventories", results);
        return "manager/inventory/list";
    }

    // REST API endpoints for AJAX calls
    @GetMapping("/api/counts/{inventoryId}")
    @ResponseBody
    public ResponseEntity<List<InventoryCountDTO>> getInventoryCounts(@PathVariable Long inventoryId) {
        return ResponseEntity.ok(inventoryService.findCountsByInventory(inventoryId));
    }

    @PostMapping("/api/counts")
    @ResponseBody
    public ResponseEntity<?> saveCount(@RequestBody InventoryCountDTO countDTO) {
        try {
            inventoryService.saveCount(inventoryCountMapper.toEntity(countDTO));
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}