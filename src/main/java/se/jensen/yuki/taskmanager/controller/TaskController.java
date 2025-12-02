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
    public ResponseEntity<?> addTask(@RequestBody Task task) {
        try {
            return ResponseEntity.ok(taskService.add(task));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTask(@PathVariable Long id) {
        return taskService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("keyword")
    public ResponseEntity<?> getTaskByKeyword(@RequestParam String keyword) {
        try {
            List<Task> tasks = taskService.findByKeyword(keyword);
            if (!tasks.isEmpty()) {
                return ResponseEntity.ok(tasks);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("status")
    public ResponseEntity<?> getTasksByStatus(TaskStatus status) {
        try {
            List<Task> tasks = taskService.findByStatus(status);
            if (!tasks.isEmpty()) {
                return ResponseEntity.ok(tasks);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task task) {
        try {
            return ResponseEntity.ok(taskService.update(id, task));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/update-status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody TaskStatus status) {
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
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
