package bbt.tao.warehouse.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// Результаты подсчета при инвентаризации
@Data
@Entity
@Table(name = "inventory_counts")
public class InventoryCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
    
    @Column(name = "expected_quantity")
    private Double expectedQuantity;
    
    @Column(name = "actual_quantity")
    private Double actualQuantity;
    
    @Column(name = "batch_number")
    private String batchNumber;
    
    @Column(name = "count_date")
    private LocalDateTime countDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counted_by")
    private User countedBy;
    
    @Column
    private String notes;
} 