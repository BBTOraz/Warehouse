package bbt.tao.warehouse.service;

import bbt.tao.warehouse.dto.audit.AuditLogDTO;
import bbt.tao.warehouse.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface AuditLogService {

    List<AuditLogDTO> findAllLogs();

    List<AuditLogDTO> findLogsByUser(Long userId);

    List<AuditLogDTO> findLogsByAction(String actionType);

    List<AuditLogDTO> findLogsByEntity(String entityType, Long entityId);

    List<AuditLogDTO> findLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<AuditLogDTO> findRecentActionsByType(String actionType, LocalDateTime since);

    void logAction(User user, String actionType, String entityType, Long entityId, String actionDetails, String ipAddress);
}
