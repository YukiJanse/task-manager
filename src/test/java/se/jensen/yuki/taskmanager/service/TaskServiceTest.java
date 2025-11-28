package se.jensen.yuki.taskmanager.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import se.jensen.yuki.taskmanager.model.Task;
import se.jensen.yuki.taskmanager.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    TaskRepository taskRepository;
    @InjectMocks
    TaskService taskService;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Get all tasks")
    void getAllTasks() {
        // Arrange
        Mockito.when(taskRepository.findAll()).thenReturn(List.of(
                new Task(1L, "test", "This is test", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now())));

        // Act
        List<Task> tasks = taskService.getAllTasks();

        // Assert
        assertEquals(1, tasks.size());
    }

    @Test
    @DisplayName("Get a task by ID successfully")
    void getByIdSuccess() {
        // Arrange
        Optional<Task> optionalTask = Optional.of(
                new Task(1L, "test", "This is test",
                        LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()));
        Mockito.when(taskRepository.findById(Mockito.anyLong()))
                .thenReturn(optionalTask);

        // Act
        Optional<Task> returnOpt = taskService.getById(1L);

        // Act
        assertTrue(returnOpt.isPresent());
    }

    @Test
    @DisplayName("Fail getting a task by unexisted ID")
    void getByIdFailWithUnexistedId() {
        // Arrange
        Mockito.when(taskRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act
        Optional<Task> returnOpt = taskService.getById(99L);

        // Act
        assertFalse(returnOpt.isPresent());
    }

    @Test
    @DisplayName("Fail getting a task by a negative number ID")
    void getByIdFailWithNegativeNumberId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.getById(-1L));
    }

    @Test
    @DisplayName("Fail getting a task by a negative number ID")
    void getByIdFailWithZeroNumberId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.getById(0L));
    }

    @Test
    @DisplayName("Add a task successfully")
    void addTaskSuccess() {
        // Arrange
        Task task = new Task(null, "test", "This is test",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
        Task savedTask = new Task(1L, "test", "This is test",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
        Mockito.when(taskRepository.save(task))
                .thenReturn(savedTask);

        // Act
        Task result = taskService.add(task);

        // Assert
        assertEquals(savedTask.getId(), result.getId());
    }

    @Test
    @DisplayName("Fail adding a task")
    void addTaskSFail() {
        // Arrange
        Task emptyTask = null;
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.add(emptyTask));
    }
//
//    @Test
//    void update() {
//    }
//
//    @Test
//    void findByKeyword() {
//    }
//
//    @Test
//    void findByCompleted() {
//    }
//
//    @Test
//    void deleteTask() {
//    }
}