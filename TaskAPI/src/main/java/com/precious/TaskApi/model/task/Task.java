package com.precious.TaskApi.model.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

import com.precious.TaskApi.model.enums.TaskCategory;
import com.precious.TaskApi.model.enums.TaskStatus;
import com.precious.TaskApi.model.enums.TaskType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.InheritanceType;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "tasks")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String clientId;

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

    private TaskType type = TaskType.TRAINING; // Default is Training

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private TaskCategory category;

    @Column(name = "task_quality")
    private Double taskQuality;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "priority", nullable = false)
    private Integer priority = 0; // Default priority is 0 (lowest) 1 (medium) 2 (high)

    private TaskStatus status = TaskStatus.PENDING; // Default status is PENDING

    private LocalDateTime deadline;

    private Duration durationPerTask;
}