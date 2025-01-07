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

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    // File uploaded by the client
    @NotNull(message = "Task file is required")
    private MultipartFile mainTaskFile;

    @NotBlank(message = "Instruction is required")
    private String instructions;

    // Title and description of the task
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    // Reward associated with the task
    @Positive(message = "Reward must be positive")
    private double reward;

    // Task category (e.g., training, work, exam)
    @NotNull(message = "Category is required")
    private TaskCategory category;

    // Task deadline
    @NotNull(message = "Deadline is required")
    private LocalDateTime deadline;

    // Creation timestamp, auto-generated
    private LocalDateTime createdAt = LocalDateTime.now();
}