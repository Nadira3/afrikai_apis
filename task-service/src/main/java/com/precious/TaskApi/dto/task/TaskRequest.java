package com.precious.TaskApi.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;

import com.precious.TaskApi.model.enums.TaskCategory;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Task Request Data Transfer Object")
public class TaskRequest {
    // File uploaded by the client
    @NotNull(message = "Task file is required")
    @Schema(description = "File to import", required = true)
    private MultipartFile mainTaskFile;

    @NotBlank(message = "Instruction is required")
    @Schema(description = "Instruction for the task", required = true)
    private String instructions;

    // Title and description of the task
    @NotBlank(message = "Title is required")
    @Schema(description = "Title of the task", required = true)
    private String title;

    @NotBlank(message = "Description is required")
    @Schema(description = "Description of the task", required = true)
    private String description;

    // Reward associated with the task
    @Positive(message = "Reward must be positive")
    @Schema(description = "Reward for the task", required = true)
    private double reward;

    // Task category (e.g., training, work, exam)
    @NotNull(message = "Category is required")
    @Schema(description = "Category of the task", required = true)
    private TaskCategory category;

    // Task deadline
    @NotNull(message = "Deadline is required")
    @Schema(description = "Deadline for the task", required = true)
    private LocalDateTime deadline;

    // Creation timestamp, auto-generated
    @Schema(description = "Timestamp of the task creation; instantiated automatically when a new task is created")
    private LocalDateTime createdAt = LocalDateTime.now();
}
