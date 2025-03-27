package bbt.tao.warehouse.controller;

import bbt.tao.warehouse.dto.audit.AuditLogDTO;
import bbt.tao.warehouse.dto.inventory.InventoryDTO;
import bbt.tao.warehouse.dto.inventory.InventoryTransactionDTO;
import bbt.tao.warehouse.dto.product.ProductDTO;
import bbt.tao.warehouse.dto.task.TaskDTO;
import bbt.tao.warehouse.dto.user.UserDTO;
import bbt.tao.warehouse.mapper.ProductMapper;
import bbt.tao.warehouse.model.enums.*;
import bbt.tao.warehouse.repository.InventoryCountRepository;
import bbt.tao.warehouse.service.*;
import bbt.tao.warehouse.service.impl.InventoryTransactionServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class MainController {

    private final ProductService productService;
    private final InventoryService inventoryService;
    private final AuditLogService auditLogService;
    private final TaskService taskService;
    private final UserService userService;
    private final InventoryTransactionServiceImpl transactionService;
    private final SupplierService supplierService;
    private final CustomerService customerService;
    private final WarehouseService warehouseService;
    private final CategoryService categoryService;
    private final InventoryCountRepository inventoryCountRepository;
    private final ProductMapper productMapper;

    @GetMapping("/dashboard")
    public String showDashboardPage(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("username", username);
        model.addAttribute("currentDate", LocalDate.now());

        // Add common attributes for all users
        List<TaskDTO> pendingTasks = taskService.getPendingTasksForUser(username);
        model.addAttribute("tasks", pendingTasks);
        model.addAttribute("completedTasksCount", taskService.getCompletedTasksCount(username));

        // Role-specific attributes
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            addAdminAttributes(model);
        }

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER"))) {
            addManagerAttributes(model);
        }

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_WAREHOUSE_WORKER"))) {
            addWarehouseWorkerAttributes(model);
        }

        return "dashboard";
    }

    private void addAdminAttributes(Model model) {
        // User management statistics
        List<UserDTO> allUsers = userService.findAllUsers();
        model.addAttribute("totalUsers", allUsers.size());
        model.addAttribute("activeUsers", userService.findAllActiveUsers().size());

        // User activity - last logins
        model.addAttribute("recentLogins", allUsers.stream()
                .filter(user -> user.getLastLogin() != null)
                .sorted(Comparator.comparing(UserDTO::getLastLogin).reversed())
                .limit(5)
                .collect(Collectors.toList()));

        // Audit logs
        List<AuditLogDTO> recentActivities = auditLogService.findAllLogs();
        model.addAttribute("recentActivities", recentActivities.stream()
                .sorted((a1, a2) -> a2.getActionTimestamp().compareTo(a1.getActionTimestamp()))
                .limit(10)
                .collect(Collectors.toList()));


        model.addAttribute("adminUsers", userService.findUsersByRole(RoleType.ADMIN).size());
        model.addAttribute("managerUsers", userService.findUsersByRole(RoleType.MANAGER).size());
        model.addAttribute("workerUsers", userService.findUsersByRole(RoleType.WAREHOUSE_WORKER).size());
    }

    private void addManagerAttributes(Model model) {
        // Inventory statistics
        model.addAttribute("totalProducts", productService.countAllProducts());
        model.addAttribute("lowStockCount", productService.getLowStockCount());
        model.addAttribute("lowStockProducts", productService.findProductsWithLowStock(2.0));

        // Inventory activity
        List<InventoryDTO> pendingInventories = inventoryService.findAllInventories();
        model.addAttribute("pendingOrdersCount", pendingInventories.size());
        model.addAttribute("pendingInventories", pendingInventories);
        model.addAttribute("plannedInventories", inventoryService.findInventoriesByStatus(InventoryStatus.PLANNED));

        List<InventoryTransactionDTO> recentTransactions = transactionService.findAllTransactions().stream().sorted(Comparator.comparing(InventoryTransactionDTO::getTransactionDate).reversed()).limit(5).toList();
        model.addAttribute("recentTransactions", recentTransactions);
        System.out.println(recentTransactions.stream().filter(Predicate.not(transaction -> !transaction.getTransactionType().equals(TransactionType.TRANSFER))).toList());

        model.addAttribute("receivingCount", transactionService.findTransactionsByType(TransactionType.RECEIVING).size());
        model.addAttribute("shippingCount", transactionService.findTransactionsByType(TransactionType.SHIPPING).size());
        model.addAttribute("transferCount", transactionService.findTransactionsByType(TransactionType.TRANSFER).size());
        model.addAttribute("adjustmentCount", transactionService.findTransactionsByType(TransactionType.ADJUSTMENT).size());

        model.addAttribute("activeSuppliers", supplierService.findAllActiveSuppliers().size());
        model.addAttribute("activeCustomers", customerService.findAllActiveCustomers().size());
        model.addAttribute("warehousesCount", warehouseService.findAllActiveWarehouses().size());
    }

    private void addWarehouseWorkerAttributes(Model model) {
        // Assigned tasks
        String username = (String) model.getAttribute("username");
        model.addAttribute("pendingTasksCount", taskService.getPendingTasksForUser(username).size());

        model.addAttribute("lowStockProducts", inventoryCountRepository.findAll().stream()
                .filter(inventoryCount -> inventoryCount.getActualQuantity() < inventoryCount.getProduct().getMinStockLevel() + 4.0)
                .map(inventoryCount -> {
                    ProductDTO productDTO = productMapper.toDTO(inventoryCount.getProduct());
                    productDTO.setCurrentStock(inventoryCount.getActualQuantity());
                    productDTO.setLocation(inventoryCount.getLocation().getWarehouse().getName());
                    return productDTO;
                })
                .collect(Collectors.toList()));

        // Active inventories
        List<InventoryDTO> activeInventories = inventoryService.findInventoriesByStatus(InventoryStatus.IN_PROGRESS);
        model.addAttribute("activeInventories", activeInventories);

        // Warehouse statistics
        model.addAttribute("warehouseCount", warehouseService.findAllActiveWarehouses().size());
        model.addAttribute("totalProducts", productService.countAllProducts());

        // Recently completed tasks
        model.addAttribute("completedTasks", taskService.getCompletedTasksCount(username));
    }
}