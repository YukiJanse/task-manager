package se.jensen.yuki.taskmanager.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import se.jensen.yuki.taskmanager.model.Task;
import se.jensen.yuki.taskmanager.model.TaskStatus;

import java.util.List;

@Repository
public class TaskRepositoryImpl implements TaskRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Task> findByStatus(TaskStatus status) {
        String jpql = "SELECT t FROM Task t WHERE t.status = :status";

        return em.createQuery(jpql, Task.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<Task> findByKeyword(String keyword) {
        String jpql = """
                SELECT t FROM Task t
                WHERE LOWER(t.title) LIKE LOWER(:keyword)
                OR LOWER(t.description) LIKE LOWER(:keyword)
                """;
        String pattern = "%" + keyword + "%";
        return em.createQuery(jpql, Task.class)
                .setParameter("keyword", pattern)
                .getResultList();
    }
}
