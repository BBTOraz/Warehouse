package bbt.tao.warehouse.mapper;

import bbt.tao.warehouse.dto.audit.AuditLogDTO;
import bbt.tao.warehouse.model.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    @Mapping(target = "username", expression = "java(auditLog.getUser() != null ? auditLog.getUser().getUsername() : null)")
    @Mapping(target = "userID", expression = "java(auditLog.getUser() != null ? auditLog.getUser().getId() : null)")
    AuditLogDTO toDTO(AuditLog auditLog);

    @Mapping(target = "user", ignore = true)
    AuditLog toEntity(AuditLogDTO dto);
}
