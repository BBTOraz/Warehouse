package bbt.tao.warehouse.dto.audit;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogDTO {
    private Long id;
    private String username;
    private Long userID;
    private String actionType;
    private String entityType;
    private Long entityId;
    private String actionDetails;
    private String ipAddress;
    private LocalDateTime actionTimestamp;
}

