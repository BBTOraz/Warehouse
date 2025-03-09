package bbt.tao.warehouse.model;

import jakarta.persistence.*;
import lombok.Data;

// Клиент
@Data
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column
    private String contactPerson;
    
    @Column
    private String phone;
    
    @Column
    private String email;
    
    @Column
    private String address;
    
    @Column(name = "tax_id")
    private String taxId;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
} 