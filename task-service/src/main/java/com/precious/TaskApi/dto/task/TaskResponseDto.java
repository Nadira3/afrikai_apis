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
    private UUID id; // UUID for created task if available
    private String title;
    private String description;
    private TaskStatus status;
    private double reward;
    private LocalDateTime createdAt;
    private TaskCategory category;

    // Template to return for processinf errors
public static TaskResponseDto toErrorTemplate(TaskCreationDto request) {
	TaskResponseDto taskResponseDto = new TaskResponseDto();
	taskResponseDto.setTitle(request.getTitle());
	taskResponseDto.setDescription("Error processing task");
	taskResponseDto.setStatus(TaskStatus.FAILED);
	taskResponseDto.setReward(request.getReward());
	taskResponseDto.setCreatedAt(LocalDateTime.now());
	taskResponseDto.setCategory(request.getCategory());
	return taskResponseDto;
}
}
