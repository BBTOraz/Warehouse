package bbt.tao.warehouse.repository;

import bbt.tao.warehouse.model.AuditLog;
import bbt.tao.warehouse.model.enums.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUser_Id(Long userId);

    List<AuditLog> findByActionTypeAndEntityTypeAndEntityId(ActionType actionType, String entityType, Long entityId);

    List<AuditLog> findByActionTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT a FROM AuditLog a WHERE a.actionType = :actionType AND a.actionTimestamp > :since ORDER BY a.actionTimestamp DESC")
    List<AuditLog> findRecentActionsByType(@Param("actionType") ActionType actionType, @Param("since") LocalDateTime since);
}