package com.precious.TaskApi.dto.task;

import java.time.LocalDateTime;
import java.util.UUID;

import com.precious.TaskApi.model.enums.TaskCategory;
import com.precious.TaskApi.model.enums.TaskStatus;
import com.precious.TaskApi.model.task.Task;

import lombok.Data;

// Task Response DTO
@Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class TaskResponse {
    private UUID id; // UUID for created task if available
    private String title;
    private String description;
    private TaskStatus status;
    private double reward;
    private LocalDateTime createdAt;
    private TaskCategory category;

    // Template to return for processing errors
    public static TaskResponse toErrorTemplate(TaskRequest request) {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setTitle(request.getTitle());
        taskResponse.setDescription("Error processing task");
        taskResponse.setStatus(TaskStatus.FAILED);
        taskResponse.setReward(request.getReward());
        taskResponse.setCreatedAt(LocalDateTime.now());
        taskResponse.setCategory(request.getCategory());
        return taskResponse;
    }

    public static TaskResponse fromEntity(Task task) {
       return new TaskResponse(task.getId(),
        task.getTitle(), task.getDescription(), task.getStatus(), task.getReward(), task.getCreatedAt(), task.getCategory());
    }

    public static TaskResponse toErrorTemplate(String string) {
       return new TaskResponse(null, string, string, TaskStatus.FAILED, 0.0, LocalDateTime.now(), TaskCategory.OTHER);
    }
}  