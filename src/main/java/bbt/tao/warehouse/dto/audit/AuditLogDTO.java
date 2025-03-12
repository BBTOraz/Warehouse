package bbt.tao.warehouse.dto.audit;


import bbt.tao.warehouse.dto.user.UserDTO;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogDTO {
    private Long id;
    private Long userID;
    private String actionType;
    private String entityType;
    private Long entityId;
    private String actionDetails;
    private String ipAddress;
    private LocalDateTime actionTimestamp;
}

