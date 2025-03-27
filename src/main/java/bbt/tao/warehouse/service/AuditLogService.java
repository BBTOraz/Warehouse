package bbt.tao.warehouse.service;

import bbt.tao.warehouse.dto.audit.AuditLogDTO;
import bbt.tao.warehouse.model.User;
import bbt.tao.warehouse.model.enums.ActionType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface AuditLogService {

    List<AuditLogDTO> findAllLogs();

    List<AuditLogDTO> findLogsByUser(Long userId);

    List<AuditLogDTO> findLogsByAction(ActionType actionType);

    List<AuditLogDTO> findLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<AuditLogDTO> findRecentActionsByType(ActionType actionType, LocalDateTime since);

    void logAction(User user, ActionType actionType, String entityType, Long entityId, String actionDetails, String ipAddress);
}
