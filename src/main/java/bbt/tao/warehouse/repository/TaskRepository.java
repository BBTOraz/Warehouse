package bbt.tao.warehouse.repository;

import bbt.tao.warehouse.model.Task;
import bbt.tao.warehouse.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignedUser_UsernameAndStatus(String username, TaskStatus status);

    List<Task> findByAssignedUser_Username(String username);
}
