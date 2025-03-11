package bbt.tao.warehouse.model;

import bbt.tao.warehouse.model.enums.PermissionType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Разрешения
@Data
@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PermissionType name;
    
    @Column
    private String description;
    
    @ManyToMany(mappedBy = "permissions")
    private List<Role> roles = new ArrayList<>();
} 