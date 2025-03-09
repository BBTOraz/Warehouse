package bbt.tao.warehouse.service.impl;

import bbt.tao.warehouse.model.AuditLog;
import bbt.tao.warehouse.model.User;
import bbt.tao.warehouse.repository.AuditLogRepository;
import bbt.tao.warehouse.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public List<AuditLog> findAllLogs() {
        return auditLogRepository.findAll();
    }

    @Override
    public List<AuditLog> findLogsByUser(Long userId) {
        return auditLogRepository.findByUser_Id(userId);
    }

    @Override
    public List<AuditLog> findLogsByAction(String actionType) {
        return auditLogRepository.findRecentActionsByType(actionType, LocalDateTime.now().minusYears(100));
    }

    @Override
    public List<AuditLog> findLogsByEntity(String entityType, Long entityId) {
        return auditLogRepository.findByActionTypeAndEntityTypeAndEntityId("ALL", entityType, entityId);
    }

    @Override
    public List<AuditLog> findLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByActionTimestampBetween(startDate, endDate);
    }

    @Override
    public List<AuditLog> findRecentActionsByType(String actionType, LocalDateTime since) {
        return auditLogRepository.findRecentActionsByType(actionType, since);
    }

    @Override
    public void logAction(User user, String actionType, String entityType, Long entityId, String actionDetails, String ipAddress) {
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setActionType(actionType);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setActionDetails(actionDetails);
        log.setIpAddress(ipAddress);
        log.setActionTimestamp(LocalDateTime.now());

        auditLogRepository.save(log);
    }
}
