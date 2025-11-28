package se.jensen.yuki.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.jensen.yuki.taskmanager.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long>, TaskRepositoryCustom {

}
