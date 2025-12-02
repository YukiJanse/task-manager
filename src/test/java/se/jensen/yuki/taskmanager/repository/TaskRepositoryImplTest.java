package se.jensen.yuki.taskmanager.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import se.jensen.yuki.taskmanager.model.Task;
import se.jensen.yuki.taskmanager.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class TaskRepositoryImplTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("my_database")
            .withUsername("taskmanager")
            .withPassword("pass");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        taskRepository.save(new Task("test1", "This is a test task",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()));
        taskRepository.save(new Task("test2", "This is a test task",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()));
    }


    @Test
    @DisplayName("Find tasks by status successfully")
    void findByStatusSuccess() {
        // Act
        List<Task> tasks = taskRepository.findByStatus(TaskStatus.NOT_STARTED);

        // Assert*
        assertEquals(2, tasks.size());
    }

    @Test
    @DisplayName("Find tasks by status but no tasks found")
    void findByStatusNotFound() {
        // Act
        List<Task> tasks = taskRepository.findByStatus(TaskStatus.DONE);

        // Assert*
        assertEquals(0, tasks.size());
    }


    @Test
    @DisplayName("Find tasks by keyword successfully")
    void findByKeywordSuccess() {
        // Act
        List<Task> tasks = taskRepository.findByKeyword("test");

        // Assert
        assertEquals(2, tasks.size());
        assertTrue(tasks.get(0).getTitle().contains("test") || tasks.get(0).getDescription().contains("test"));
        assertTrue(tasks.get(1).getTitle().contains("test") || tasks.get(1).getDescription().contains("test"));
    }

    @Test
    @DisplayName("Find tasks by keyword but no tasks found")
    void findByKeywordNotFound() {
        // Act
        List<Task> tasks = taskRepository.findByKeyword("Java");

        // Assert
        assertEquals(0, tasks.size());
    }
}