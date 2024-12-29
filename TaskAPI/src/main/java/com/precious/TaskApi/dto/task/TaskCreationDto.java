package com.precious.TaskApi.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

import com.precious.TaskApi.model.enums.TaskCategory;
import com.precious.TaskApi.model.enums.TaskType;

// Task Creation DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreationDto {
    @NotBlank(message = "ClientId is required")
    private String clientId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @Positive(message = "Reward must be positive")
    private double reward;

    @NotNull(message = "Category is required")
    private TaskCategory category;

    private TaskType type = TaskType.TRAINING; // Default is TASK

    @NotNull(message = "Deadline is required")
    private LocalDateTime deadline;

    @Positive(message = "Priority must be positive")
    @NotBlank(message = "Priority is required")
    private Integer priority;

    @NotNull(message = "Admin ID is required")
    private Long createdBy;

    private int questionNumber;

    @NotNull(message = "Duration per task is required")
    private Duration durationPerTask;
}
