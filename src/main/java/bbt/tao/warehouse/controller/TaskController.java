package bbt.tao.warehouse.controller;

import bbt.tao.warehouse.dto.audit.AuditLogDTO;
import bbt.tao.warehouse.dto.task.TaskDTO;
import bbt.tao.warehouse.dto.user.UserDTO;
import bbt.tao.warehouse.mapper.UserMapper;
import bbt.tao.warehouse.model.enums.ActionType;
import bbt.tao.warehouse.model.enums.RoleType;
import bbt.tao.warehouse.security.CustomUserDetails;
import bbt.tao.warehouse.service.AuditLogService;
import bbt.tao.warehouse.service.TaskService;
import bbt.tao.warehouse.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final AuditLogService auditLogService;
    private final UserMapper userMapper;

    public TaskController(TaskService taskService, UserService userService, AuditLogService auditLogService, UserMapper userMapper) {
        this.taskService = taskService;
        this.userService = userService;
        this.auditLogService = auditLogService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public String getMyTasks(@RequestParam(required = false) String search,
                             @RequestParam(required = false) String sort,
                             Model model,
                             Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserDTO currentUser = userDetails.getUser();

        // Получаем список всех задач через TaskService
        List<TaskDTO> allTasks = taskService.findAllDetailedTasks();
        List<TaskDTO> tasks;

        // Определяем роль текущего пользователя
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> r.getRole() == RoleType.ADMIN);
        boolean isManager = currentUser.getRoles().stream().anyMatch(r -> r.getRole() == RoleType.MANAGER);
        boolean isWorker = currentUser.getRoles().stream().anyMatch(r -> r.getRole() == RoleType.WAREHOUSE_WORKER);

        if (isAdmin) {
            System.out.println("Admin role detected");
            tasks = allTasks; // Админ видит все задачи
        } else if (isManager) {
            System.out.println("Manager role detected");
            // Менеджер видит задачи, назначенные менеджерам и работникам склада
            tasks = allTasks.stream()
                    .filter(t -> t.getAssignedUserDetails() != null && t.getAssignedUserDetails().getRoles() != null &&
                            t.getAssignedUserDetails().getRoles().stream().anyMatch(r -> r.getRole() == RoleType.MANAGER ||
                                    r.getRole() == RoleType.WAREHOUSE_WORKER))
                    .collect(Collectors.toList());
        } else {
            System.out.println("Warehouse worker role detected");
            // Работник видит только свои задачи
            tasks = allTasks.stream()
                    .filter(t -> t.getAssignedUserDetails() != null && t.getAssignedUserDetails().getId().equals(currentUser.getId()))
                    .collect(Collectors.toList());
        }

        // Фильтрация по поисковому запросу (по заголовку или описанию задачи)
        if (search != null && !search.isEmpty()) {
            String lowerSearch = search.toLowerCase();
            tasks = tasks.stream()
                    .filter(t -> (t.getTitle() != null && t.getTitle().toLowerCase().contains(lowerSearch)) ||
                            (t.getDescription() != null && t.getDescription().toLowerCase().contains(lowerSearch)))
                    .collect(Collectors.toList());
        }

        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "По сроку выполнения":
                    tasks = tasks.stream()
                            .sorted(Comparator.comparing(TaskDTO::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())))
                            .collect(Collectors.toList());
                    break;
                case "По приоритету":
                    tasks = tasks.stream()
                            .sorted(Comparator.comparing(TaskDTO::getPriority, Comparator.nullsLast(Comparator.naturalOrder())))
                            .collect(Collectors.toList());
                    break;
                case "По дате создания":
                    tasks = tasks.stream()
                            .sorted(Comparator.comparing(TaskDTO::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                            .collect(Collectors.toList());
                    break;
                default:
                    break;
            }
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("sortOptions", List.of("По сроку выполнения", "По приоритету", "По дате создания"));
        model.addAttribute("searchKeyword", search != null ? search : "");
        model.addAttribute("userRole", isAdmin ? "ADMIN" : isManager ? "MANAGER" : "WAREHOUSE_WORKER");

        if (isAdmin || isManager) {
            List<UserDTO> potentialAssignees = userService.findAllUsers();
            if (isManager) {
                potentialAssignees = potentialAssignees.stream()
                        .filter(u -> u.getRoles().stream().anyMatch(r -> r.getRole() == RoleType.MANAGER ||
                                r.getRole() == RoleType.WAREHOUSE_WORKER))
                        .collect(Collectors.toList());
            }
            model.addAttribute("potentialAssignees", potentialAssignees);
        }
        return "mytask";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String createTask(@ModelAttribute("task") TaskDTO taskDTO,
                             Authentication authentication,
                             Model model) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserDTO currentUser = userDetails.getUser();
        Optional<UserDTO> assignee;
        if (currentUser.getRoles().stream().anyMatch(r -> r.getRole() != RoleType.WAREHOUSE_WORKER)) {
            if (taskDTO.getAssignedUserDetails().getId() != null) {
                assignee = userService.findUserById(taskDTO.getAssignedUserDetails().getId());
                if (assignee.isPresent()) {
                    boolean validAssignee = assignee.get().getRoles().stream().anyMatch(r -> r.getRole() == RoleType.MANAGER ||
                            r.getRole() == RoleType.ADMIN);
                    log.info("Valid assignee: {}", validAssignee);
                    if (validAssignee) {
                        auditLogService.logAction(userMapper.toEntity(currentUser), ActionType.CREATE, "USER", 1L, "Создание задачи: " + taskDTO.getTitle() + " для " + assignee.get().getUsername(), "192.168.1.101" );
                        taskService.saveTask(taskDTO);
                            return "redirect:/tasks";
                    }
                }
            }
        }
        model.addAttribute("error", "Менеджер может создавать задачи только для менеджеров и работников склада.");

        return "redirect:/tasks";
    }
}
