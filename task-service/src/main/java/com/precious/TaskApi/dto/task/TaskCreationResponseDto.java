package com.precious.TaskApi.dto.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import com.precious.TaskApi.model.enums.TaskCategory;
import com.precious.TaskApi.model.enums.TaskStatus;
import com.precious.TaskApi.model.task.Task;

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
		return new TaskCreationResponseDto(task.getTitle(), task.getCategory(), task.getDeadline(), task.getStatus(),
				task.getId());
	}
}
