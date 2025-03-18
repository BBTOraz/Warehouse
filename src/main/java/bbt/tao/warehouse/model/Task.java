package bbt.tao.warehouse.model;

import bbt.tao.warehouse.model.enums.TaskPriority;
import bbt.tao.warehouse.model.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Название задачи (обязательно)
    @Column(nullable = false)
    private String title;

    // Описание задачи (опционально)
    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;

    @Column
    private LocalDateTime dueDate;

    // Дата создания задачи
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Дата последнего обновления задачи
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

