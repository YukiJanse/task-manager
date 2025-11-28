package se.jensen.yuki.taskmanager.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.jensen.yuki.taskmanager.model.Task;
import se.jensen.yuki.taskmanager.model.TaskStatus;
import se.jensen.yuki.taskmanager.repository.TaskRepository;
import se.jensen.yuki.taskmanager.service.TaskService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "*")
@Tag(name = "tasks", description = "Handle all tasks for task manager")
public class TaskController {
    private final TaskService taskService;
    private final TaskRepository taskRepository;

    public TaskController(TaskService taskService, TaskRepository taskRepository) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
    }

    @PostMapping
    public Task addTask(@RequestBody Task task) {
        return taskService.add(task);
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        return taskService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("keyword")
    public List<Task> getTaskByKeyword(@RequestParam String keyword) {
        return taskService.findByKeyword(keyword);
    }

    @GetMapping("status")
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskService.findByStatus(status);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        try {
            return ResponseEntity.ok(taskService.update(id, task));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/update-status")
    public ResponseEntity<Task> updateStatus(@PathVariable Long id, @RequestBody TaskStatus status) {
        if (status == null) {
            return ResponseEntity.badRequest().build();
        }
        return taskRepository.findById(id)
                .map(task -> {
                    task.setStatus(status);
                    Task savedTask = taskRepository.save(task);
                    return ResponseEntity.ok(savedTask);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

}
