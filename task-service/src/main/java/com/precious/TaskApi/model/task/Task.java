package com.precious.TaskApi.model.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.precious.TaskApi.dto.task.TaskRequest;
import com.precious.TaskApi.dto.task.TaskResponse;
import com.precious.TaskApi.model.enums.TaskCategory;
import com.precious.TaskApi.model.enums.TaskStatus;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.Table;
import jakarta.persistence.InheritanceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "tasks")
@Inheritance(strategy = InheritanceType.JOINED)
public class Task {
    @Id
    private final UUID id = UUID.randomUUID();

    private String clientId;

    private UUID importId;

    @Column(name = "main_file_url")
    private String mainFileUrl;

    private String instructions;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "reward", nullable = false)
    private Double reward;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Builder.Default
    @ElementCollection
    private List<Long> assignedUserIds = new ArrayList<Long>();

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private TaskCategory category;

    @Column(name = "priority", nullable = false)
    @Builder.Default
    private Integer priority = 0; // Default priority is 0 (lowest) 1 (medium) 2 (high)

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.CREATED; // Default status is PENDING

    private LocalDateTime deadline;

    private Duration durationPerTask;

    // map to the taskCreation dto
    public Task fromTaskRequest(TaskRequest taskCreationDto) {
        this.title = taskCreationDto.getTitle();
        this.description = taskCreationDto.getDescription();
        this.reward = taskCreationDto.getReward();
        this.category = taskCreationDto.getCategory();
        this.deadline = taskCreationDto.getDeadline();
        return this;
    }

    // map to TaskResponseDto object
    public TaskResponse fromEntity() {
        TaskResponse taskResponse = new TaskResponse();

        taskResponse.setId(this.id);
        taskResponse.setTitle(this.title);
        taskResponse.setDescription(this.description);
        taskResponse.setStatus(this.status);
        taskResponse.setReward(this.reward);
        taskResponse.setCreatedAt(this.createdAt);

        return taskResponse;

    }

    public void setAssignedUserIds(List<Long> userIds) {
        if (userIds != null) {
            this.assignedUserIds.addAll(userIds);
        }
    }
}
