package bbt.tao.warehouse.attributes;


import bbt.tao.warehouse.dto.task.TaskDTO;
import bbt.tao.warehouse.service.TaskService;
import groovyjarjarantlr4.v4.runtime.misc.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
@AllArgsConstructor
public class GlobalModelAttributes {

    private final TaskService taskService;

    @ModelAttribute
    public void addAttributes(Model model, @Nullable Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("username", username);
            List<TaskDTO> pendingTasks = taskService.getPendingTasksForUser(username);
            model.addAttribute("taskPending", pendingTasks);
        } else {
            model.addAttribute("username", "Гость");
            model.addAttribute("taskPending", Collections.emptyList());
        }
        model.addAttribute("currentDate", LocalDate.now());
    }
}
