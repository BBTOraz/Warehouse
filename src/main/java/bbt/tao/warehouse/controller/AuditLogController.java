package bbt.tao.warehouse.controller;

import bbt.tao.warehouse.model.AuditLog;
import bbt.tao.warehouse.service.AuditLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditLog> getAllAuditLogs() {
        return auditLogService.findAllLogs();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditLog> getAuditLogsByUser(@PathVariable Long userId) {
        return auditLogService.findLogsByUser(userId);
    }

    @GetMapping("/action/{actionType}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditLog> getAuditLogsByAction(@PathVariable String actionType) {
        return auditLogService.findLogsByAction(actionType);
    }

    @GetMapping("/date")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditLog> getAuditLogsByDateRange(@RequestParam String start, @RequestParam String end) {
        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);
        return auditLogService.findLogsByDateRange(startDate, endDate);
    }
}
