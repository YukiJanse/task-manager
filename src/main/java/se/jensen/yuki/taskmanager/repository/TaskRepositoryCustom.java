package se.jensen.yuki.taskmanager.repository;

import se.jensen.yuki.taskmanager.model.Task;
import se.jensen.yuki.taskmanager.model.TaskStatus;

import java.util.List;

public interface TaskRepositoryCustom {
    List<Task> findByKeyword(String keyword);

    List<Task> findByStatus(TaskStatus status);
}
