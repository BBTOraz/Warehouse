package bbt.tao.warehouse.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// Товар
@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 2000)
    private String description;
    
    @Column(name = "sku", nullable = false, unique = true)
    private String sku; // Уникальный идентификатор товара
    
    @Column(name = "barcode")
    private String barcode;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Column(name = "unit_of_measure")
    private String unitOfMeasure; // шт, кг, л и т.д.
    
    @Column(name = "min_stock_level")
    private Double minStockLevel; // Минимальный остаток
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "weight")
    private Double weight;
    
    @Column(name = "volume")
    private Double volume;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 