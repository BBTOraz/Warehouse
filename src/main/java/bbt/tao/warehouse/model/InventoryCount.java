package bbt.tao.warehouse.model;

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
    
    @ManyToOne
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne
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
    
    @ManyToOne
    @JoinColumn(name = "counted_by")
    private User countedBy;
    
    @Column
    private String notes;
} 