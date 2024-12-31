package com.precious.TaskApi.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;

import com.precious.TaskApi.model.enums.TaskCategory;

// Task Creation DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreationResponseDto {

    // Title and description of the task
    private String title;

    // Task category (e.g., training, work, exam)
    private TaskCategory category;

    // Task deadline
    private LocalDateTime deadline;

    // Task status
    private TaskStatus status;

	// Task ID
	private UUID id;

	// map from entity to DTO
	public static TaskCreationResponseDto fromEntity(Task task) {
		return new TaskCreationResponseDto(
			task.getTitle(),
			task.getCategory(),
			task.getDeadline(),
			task.getStatus(),
			task.getId()
		);
	}
}
