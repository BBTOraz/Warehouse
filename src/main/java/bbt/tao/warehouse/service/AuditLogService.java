package bbt.tao.warehouse.service;

import bbt.tao.warehouse.model.AuditLog;
import bbt.tao.warehouse.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface AuditLogService {

    List<AuditLog> findAllLogs();

    List<AuditLog> findLogsByUser(Long userId);

    List<AuditLog> findLogsByAction(String actionType);

    List<AuditLog> findLogsByEntity(String entityType, Long entityId);

    List<AuditLog> findLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<AuditLog> findRecentActionsByType(String actionType, LocalDateTime since);

    void logAction(User user, String actionType, String entityType, Long entityId, String actionDetails, String ipAddress);
}
