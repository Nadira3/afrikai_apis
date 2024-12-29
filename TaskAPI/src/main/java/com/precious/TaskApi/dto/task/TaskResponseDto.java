package com.precious.TaskApi.dto.task;

import java.time.LocalDateTime;
import java.util.UUID;

import com.precious.TaskApi.model.enums.TaskCategory;
import com.precious.TaskApi.model.enums.TaskStatus;

import lombok.Data;

// Task Response DTO
@Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class TaskResponseDto {
    private UUID id;
    private String title;
    private String description;
    private TaskStatus status;
    private double reward;
    private LocalDateTime createdAt;
    private String clientUsername;
    private String assignedTaskerUsername;
    private TaskCategory category;
}
