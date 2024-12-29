package com.precious.TaskApi.service.task;


import com.precious.TaskApi.dto.task.TaskCreationDto;
import com.precious.TaskApi.model.task.Task;


public interface ITaskService {
    // Task Creation
    Task createTask(TaskCreationDto request);
    // // Task Discovery
    // List<Task> findAvailableTasks();
    // List<Task> findTasksByCategory(TaskCategory category);

    // // Task Assignment

    // // Task Execution
    // Task submitTask(Long taskId, String submissionDetails);

    // // Task Review
    // Task reviewTask(Long taskId, boolean approved, double qualityScore);

    // // Task Management
    // Task cancelTask(Long taskId);
    // void deleteTask(Long taskId);

    // // Retrieval Methods
    // Task getTaskById(Long taskId);
}
