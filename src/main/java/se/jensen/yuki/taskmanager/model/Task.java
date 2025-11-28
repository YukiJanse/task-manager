package se.jensen.yuki.taskmanager.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDatetime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDatetime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDatetime;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.NOT_STARTED;

    public Task() {
    }

    public Task(Long id, String title, String description, LocalDateTime createdDatetime, LocalDateTime startDatetime, LocalDateTime endDatetime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdDatetime = createdDatetime;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
    }

    public void copyFrom(Task other) {
        this.title = other.title != null ? other.title : this.title;
        this.description = other.description != null ? other.description : this.description;
        this.createdDatetime = other.createdDatetime != null ? other.createdDatetime : this.createdDatetime;
        this.startDatetime = other.startDatetime != null ? other.startDatetime : this.startDatetime;
        this.endDatetime = other.endDatetime != null ? other.endDatetime : this.endDatetime;
        this.status = other.status != null ? other.status : this.status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(LocalDateTime createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public LocalDateTime getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(LocalDateTime startDatetime) {
        this.startDatetime = startDatetime;
    }

    public LocalDateTime getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(LocalDateTime endDatetime) {
        this.endDatetime = endDatetime;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus nextStatus) {
        status = nextStatus;
    }
}
