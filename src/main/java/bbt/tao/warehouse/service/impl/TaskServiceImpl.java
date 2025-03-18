package bbt.tao.warehouse.service.impl;

import bbt.tao.warehouse.dto.task.TaskDTO;
import bbt.tao.warehouse.mapper.TaskMapper;
import bbt.tao.warehouse.model.Task;
import bbt.tao.warehouse.model.enums.TaskStatus;
import bbt.tao.warehouse.repository.TaskRepository;
import bbt.tao.warehouse.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для задач.
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public List<TaskDTO> getPendingTasksForUser(String username) {
        List<Task> tasks = taskRepository.findByAssignedUser_UsernameAndStatus(username, TaskStatus.PENDING);
        return tasks.stream().map(taskMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public int getCompletedTasksCount(String username) {
        List<Task> tasks = taskRepository.findByAssignedUser_Username(username);
        long count = tasks.stream().filter(task -> task.getStatus() == TaskStatus.COMPLETED).count();
        return (int) count;
    }

    @Override
    public TaskDTO saveTask(TaskDTO taskDTO) {
        if (taskDTO.getId() == null) {
            taskDTO.setCreatedAt(LocalDateTime.now());
        }
        Task task = taskMapper.toEntity(taskDTO);
        task.setUpdatedAt(LocalDateTime.now());
        Task savedTask = taskRepository.save(task);
        return taskMapper.toDTO(savedTask);
    }

    @Override
    public Optional<TaskDTO> findTaskById(Long id) {
        return taskRepository.findById(id).map(taskMapper::toDTO);
    }

    @Override
    public TaskDTO updateTask(TaskDTO taskDTO) {
        if (taskDTO.getId() == null) {
            throw new IllegalArgumentException("Task ID cannot be null for update operation");
        }
        Task existingTask = taskRepository.findById(taskDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskDTO.getId()));
        // Обновляем поля сущности на основе DTO
        taskMapper.updateEntityFromDTO(taskDTO, existingTask);
        existingTask.setUpdatedAt(LocalDateTime.now());
        Task updatedTask = taskRepository.save(existingTask);
        return taskMapper.toDTO(updatedTask);
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
