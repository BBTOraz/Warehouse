package bbt.tao.warehouse.controller;

import bbt.tao.warehouse.dto.task.TaskDTO;
import bbt.tao.warehouse.dto.user.UserDTO;
import bbt.tao.warehouse.dto.audit.AuditLogDTO;
import bbt.tao.warehouse.dto.inventory.InventoryTransactionDTO;
import bbt.tao.warehouse.dto.inventory.InventoryCountDTO;
import bbt.tao.warehouse.model.enums.ActionType;
import bbt.tao.warehouse.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminUserController {

    private final UserService userService;
    private final AuditLogService auditLogService;
    private final TaskService taskService;
    private final InventoryTransactionService inventoryTransactionService;
    private final InventoryService inventoryService;

    @GetMapping("/users")
    public String listUsers(@RequestParam(required = false) String search,
                            @RequestParam(required = false) String sort,
                            Model model, Authentication authentication) {
        List<UserDTO> users = userService.findAllUsers();

        if (search != null && !search.isEmpty()) {
            String lowerSearch = search.toLowerCase();
            users = users.stream()
                    .filter(u -> (u.getUsername() != null && u.getUsername().toLowerCase().contains(lowerSearch)) ||
                            (u.getFirstName() != null && u.getFirstName().toLowerCase().contains(lowerSearch)) ||
                            (u.getLastName() != null && u.getLastName().toLowerCase().contains(lowerSearch)) ||
                            (u.getEmail() != null && u.getEmail().toLowerCase().contains(lowerSearch)))
                    .collect(Collectors.toList());
        }


        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "По имени":
                    users.sort((u1, u2) -> u1.getUsername().compareToIgnoreCase(u2.getUsername()));
                    break;
                case "По последнему входу":
                    users.sort((u1, u2) -> {
                        if(u1.getLastLogin() == null && u2.getLastLogin() == null) return 0;
                        if(u1.getLastLogin() == null) return 1;
                        if(u2.getLastLogin() == null) return -1;
                        return u2.getLastLogin().compareTo(u1.getLastLogin());
                    });
                    break;
                default:
                    break;
            }
        }

        model.addAttribute("users", users);
        model.addAttribute("active", users.stream().filter(UserDTO::getIsActive).count());
        model.addAttribute("inActive", users.stream().filter(userDTO -> !userDTO.getIsActive()).count());
        model.addAttribute("adminCount", users.stream().filter(userDTO -> userDTO.getRoles().stream().anyMatch(role -> role.getRole().getRoleName().equals("ROLE_ADMIN"))).count());
        model.addAttribute("searchKeyword", search != null ? search : "");
        model.addAttribute("sortOptions", List.of("По имени", "По последнему входу"));
        return "admin/user-list";
    }

    @GetMapping("/user_details/{id}")
    public String userDetails(@PathVariable Long id, Model model) {
        UserDTO user = userService.findUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        model.addAttribute("user", user);

        List<AuditLogDTO> auditLogs = auditLogService.findLogsByUser(id);
        model.addAttribute("auditLogs", auditLogs);

        List<TaskDTO> tasks = taskService.findTasksByAssignee(id);
        model.addAttribute("tasks", tasks);

        List<InventoryTransactionDTO> transactions = inventoryTransactionService.findTransactionsByUser(id);
        model.addAttribute("transactions", transactions);

        List<InventoryCountDTO> counts = inventoryService.findInventoryCountsByUserId(id);
        model.addAttribute("counts", counts);

        return "admin/user-details";
    }

    @GetMapping("/audit-logs")
    public String viewAuditLogs(@RequestParam(required = false) ActionType actionType,
                                @RequestParam(required = false) Long userId,
                                @RequestParam(required = false) String start,
                                @RequestParam(required = false) String end,
                                Model model) {
        log.info("Fetching audit logs with actionType: {}, userId: {}, start: {}, end: {}", actionType, userId, start, end);
        List<AuditLogDTO> logs;
        List<UserDTO> users = userService.findAllUsers();
        if (actionType != null) {
            logs = auditLogService.findLogsByAction(actionType);
        } else if (userId != null) {
            logs = auditLogService.findLogsByUser(userId);
        } else if (start != null && end != null) {
            try {
                LocalDateTime startDate = LocalDateTime.parse(start);
                LocalDateTime endDate = LocalDateTime.parse(end);
                logs = auditLogService.findLogsByDateRange(startDate, endDate);
            } catch (Exception e) {
                logs = auditLogService.findAllLogs();
            }
        } else {
            logs = auditLogService.findAllLogs();
        }

        logs.sort((a, b) -> b.getActionTimestamp().compareTo(a.getActionTimestamp()));
        model.addAttribute("allUsers", users);
        model.addAttribute("actionTypes", ActionType.values());
        model.addAttribute("auditLogs", logs);
        return "admin/audit-logs";
    }
}
