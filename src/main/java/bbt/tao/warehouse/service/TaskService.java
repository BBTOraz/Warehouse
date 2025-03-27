package bbt.tao.warehouse.service;

import bbt.tao.warehouse.dto.task.TaskDTO;

import java.util.List;
import java.util.Optional;

public interface TaskService {

    // Получить список ожидающих (pending) задач для пользователя
    List<TaskDTO> getPendingTasksForUser(String username);

    // Получить количество выполненных задач для пользователя
    int getCompletedTasksCount(String username);

    // Сохранить новую задачу
    TaskDTO saveTask(TaskDTO taskDTO);

    // Найти задачу по id
    Optional<TaskDTO> findTaskById(Long id);

    // Обновить задачу
    TaskDTO updateTask(TaskDTO taskDTO);

    // Удалить задачу по id
    void deleteTask(Long id);

    List<TaskDTO> findAllTasks();

    List<TaskDTO> findAllDetailedTasks();

    List<TaskDTO> findTasksByAssignee(Long userId);
}
