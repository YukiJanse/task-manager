package se.jensen.yuki.taskmanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.jensen.yuki.taskmanager.model.Task;
import se.jensen.yuki.taskmanager.model.TaskStatus;
import se.jensen.yuki.taskmanager.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getById(Long id) {
        if (id <= 0) {
            logger.error("ID was smaller than 1.");
            throw new IllegalArgumentException("ID must be a positive number.");
        }
        return taskRepository.findById(id);
    }

    public Task add(Task task) {
        if (task == null) {
            logger.error("Task was null");
            throw new IllegalArgumentException("Task can't be null");
        }
        return taskRepository.save(task);
    }

    public Task update(Long id, Task task) {
        if (id <= 0 || task == null) {
            logger.error("ID was negative or Task was null");
            throw new IllegalArgumentException("ID must be a positive number or Task can't be null.");
        }
        Task targetTask = taskRepository.findById(id).orElseThrow();
        targetTask.copyFrom(task);
        return taskRepository.save(targetTask);
    }

    public List<Task> findByKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            logger.error("Keyword was null");
            throw new IllegalArgumentException("Keyword can't be empty.");
        }
        logger.info("Starting findByKeyword with keyword={}", keyword);
        List<Task> tasks = taskRepository.findByKeyword(keyword);
        if (tasks.size() == 0) {
            logger.warn("No tasks found with keyword= {}", keyword);
        }
        return tasks;
        //return taskRepository.findByKeyword(keyword);
    }

    public List<Task> findByStatus(TaskStatus status) {
        if (status == null) {
            logger.error("Status can't be null");
            throw new IllegalArgumentException("Status can't be null");
        }
        return taskRepository.findByStatus(status);
    }

    public void deleteTask(Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID can't be negative");
        }
        taskRepository.deleteById(id);
    }


}
