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
import se.jensen.yuki.taskmanager.model.TaskStatus;
import se.jensen.yuki.taskmanager.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

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

    @Test
    @DisplayName("Update successfully")
    void updateSuccess() {
        // Arrange
        Task oldTask = new Task(1L, "test", "This is an old task",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
        Task newTask = new Task(1L, "test", "This is a updated task",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
        Mockito.when(taskRepository.findById(1L))
                .thenReturn(Optional.of(oldTask));
        Mockito.when(taskRepository.save(oldTask))
                .thenReturn(newTask);

        // Act
        Task result = taskService.update(1L, newTask);

        // Assert
        assertNotNull(result);
        assertEquals(newTask.getId(), result.getId());
        assertEquals(newTask.getDescription(), result.getDescription());
    }

    @Test
    @DisplayName("Fail updating with a wrong ID")
    void updateFailWithWrongId() {
        // Arrange
        Task newTask = new Task(1L, "test", "This is a updated task",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.update(0L, newTask));
    }

    @Test
    @DisplayName("Fail updating with a empty task")
    void updateFailWithEmptyTask() {
        // Arrange
        Task newTask = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.update(1L, newTask));
    }

    @Test
    @DisplayName("Fail updating with a empty task")
    void updateFailWithNotExistingId() {
        // Arrange
        Task newTask = new Task(1L, "test", "This is a updated task",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
        Mockito.when(taskRepository.findById(2L))
                .thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> taskService.update(2L, newTask));
    }

    @Test
    @DisplayName("Get tasks successfully by keyword")
    void findByKeywordSuccess() {
        // Arrange
        String keyword = "test";
        Task task1 = new Task(1L, "test1", "This is a test task",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
        Task task2 = new Task(2L, "test2", "This is a test task",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
        Mockito.when(taskRepository.findByKeyword(Mockito.anyString()))
                .thenReturn(List.of(task1, task2));

        // Act
        List<Task> results = taskService.findByKeyword(keyword);

        // Assert
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("Fail getting tasks by empty keyword")
    void findByKeywordFailWithEmptyKeyword() {
        // Arrange
        String keyword = "";

        // Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.findByKeyword(keyword));
    }

    @Test
    @DisplayName("Fail getting tasks by null string object")
    void findByKeywordFailWithNullString() {
        // Arrange
        String keyword = null;

        // Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.findByKeyword(keyword));
    }

    @Test
    @DisplayName("Fail getting tasks by wrong keyword")
    void findByKeywordFailWithWrongKeyword() {
        // Arrange
        String keyword = "aaa";
        Mockito.when(taskRepository.findByKeyword(Mockito.anyString()))
                .thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> taskService.findByKeyword(keyword));
    }

    @Test
    @DisplayName("Get tasks by status")
    void findByStatusSuccess() {
        // Arrange
        Task task = new Task(1L, "test1", "This is a test task",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
        Mockito.when(taskRepository.findByStatus(TaskStatus.NOT_STARTED))
                .thenReturn(List.of(task));

        // Act
        List<Task> results = taskService.findByStatus(TaskStatus.NOT_STARTED);

        // Assert
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Fail getting tasks by null status")
    void findByStatusFailWithNullStatus() {
        // Arrange
        TaskStatus status = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.findByStatus(status));
    }

    @Test
    @DisplayName("Delete a task successfully")
    void deleteTaskSuccess() {
        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
    }


    @Test
    @DisplayName("Fail deleting a task by wrong ID")
    void deleteTaskFail() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.deleteTask(0L));
    }
}