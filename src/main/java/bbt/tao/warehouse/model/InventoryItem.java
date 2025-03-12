package bbt.tao.warehouse.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Остаток товара
@Data
@Entity
@Table(name = "inventory_items")
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
    
    @Column(nullable = false)
    private Double quantity;
    
    @Column(name = "batch_number")
    private String batchNumber;
    
    @Column(name = "expiration_date")
    private LocalDate expirationDate;
    
    @Column(name = "last_inventory_date")
    private LocalDateTime lastInventoryDate;
} 