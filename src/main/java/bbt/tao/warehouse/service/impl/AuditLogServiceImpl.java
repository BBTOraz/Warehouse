package bbt.tao.warehouse.service.impl;

import bbt.tao.warehouse.dto.audit.AuditLogDTO;
import bbt.tao.warehouse.mapper.AuditLogMapper;
import bbt.tao.warehouse.model.AuditLog;
import bbt.tao.warehouse.model.User;
import bbt.tao.warehouse.repository.AuditLogRepository;
import bbt.tao.warehouse.service.AuditLogService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    public List<AuditLogDTO> findAllLogs() {
        List<AuditLog> logs = auditLogRepository.findAll();
        return logs.stream().map(auditLogMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<AuditLogDTO> findLogsByUser(Long userId) {
        List<AuditLog> logs = auditLogRepository.findByUser_Id(userId);
        return logs.stream().map(auditLogMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<AuditLogDTO> findLogsByAction(String actionType) {
        List<AuditLog> logs = auditLogRepository.findRecentActionsByType(actionType, LocalDateTime.now().minusYears(100));
        return logs.stream().map(auditLogMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<AuditLogDTO> findLogsByEntity(String entityType, Long entityId) {
        List<AuditLog> logs = auditLogRepository.findByActionTypeAndEntityTypeAndEntityId("ALL", entityType, entityId);
        return logs.stream().map(auditLogMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<AuditLogDTO> findLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<AuditLog> logs = auditLogRepository.findByActionTimestampBetween(startDate, endDate);
        return logs.stream().map(auditLogMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<AuditLogDTO> findRecentActionsByType(String actionType, LocalDateTime since) {
        List<AuditLog> logs = auditLogRepository.findRecentActionsByType(actionType, since);
        return logs.stream().map(auditLogMapper::toDTO).collect(Collectors.toList());
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
