package bbt.tao.warehouse.controller.api;

import bbt.tao.warehouse.dto.audit.AuditLogDTO;
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
    public List<AuditLogDTO> getAllAuditLogs() {
        return auditLogService.findAllLogs();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditLogDTO> getAuditLogsByUser(@PathVariable Long userId) {
        return auditLogService.findLogsByUser(userId);
    }

    @GetMapping("/action/{actionType}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditLogDTO> getAuditLogsByAction(@PathVariable String actionType) {
        return auditLogService.findLogsByAction(actionType);
    }

    @GetMapping("/date")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditLogDTO> getAuditLogsByDateRange(@RequestParam String start, @RequestParam String end) {
        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);
        return auditLogService.findLogsByDateRange(startDate, endDate);
    }
}
