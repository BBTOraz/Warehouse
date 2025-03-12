package bbt.tao.warehouse.model;

import jakarta.persistence.*;
import lombok.Data;

// Местоположение на складе (стеллаж, секция, ячейка)
@Data
@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String code;
    
    @Column
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;
    
    @Column(name = "max_weight")
    private Double maxWeight;
    
    @Column(name = "max_volume")
    private Double maxVolume;
} 