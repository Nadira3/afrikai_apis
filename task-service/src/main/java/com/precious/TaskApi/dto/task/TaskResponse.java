package com.precious.TaskApi.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(description = "Task Response Data Transfer Object")
public class TaskResponse {

    @Schema(description = "Task ID")
    private UUID id;

    @Schema(description = "Title of the task")
    private String title;

    @Schema(description = "Description of the task")
    private String description;

    @Schema(description = "Status of the task")
    private TaskStatus status;

    @Schema(description = "Reward for the task")
    private double reward;

    @Schema(description = "Timestamp of the task creation")
    private LocalDateTime createdAt;

    @Schema(description = "Category of the task")
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
