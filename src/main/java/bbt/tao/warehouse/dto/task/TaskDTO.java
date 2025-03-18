package bbt.tao.warehouse.dto.task;

import bbt.tao.warehouse.dto.user.UserDTO;
import bbt.tao.warehouse.model.enums.TaskPriority;
import bbt.tao.warehouse.model.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private Long assignedUser;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UserDTO assignedUserDetails; // Details of the assigned user
}
