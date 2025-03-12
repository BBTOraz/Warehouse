package bbt.tao.warehouse.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// Аудит действий пользователей
@Data
@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "action_type", nullable = false)
    private String actionType;
    
    @Column(name = "entity_type")
    private String entityType;
    
    @Column(name = "entity_id")
    private Long entityId;
    
    @Column(name = "action_details", length = 4000)
    private String actionDetails;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "action_timestamp", nullable = false)
    private LocalDateTime actionTimestamp;
} 