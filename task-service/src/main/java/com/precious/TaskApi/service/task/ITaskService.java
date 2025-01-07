package com.precious.TaskApi.service.task;

import java.util.UUID;

import com.precious.TaskApi.dto.task.TaskRequest;
import com.precious.TaskApi.model.task.Task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITaskService {
    // Task Creation
    Task createTask(TaskRequest request, String clientId);
    
    void deleteAll();

    Task getTaskById(UUID id);

    Page<Task> getTaskByClientId(String clientId, Pageable pageable);

    Page<Task> getTaskByUserId(Long userId, Pageable pageable);

    Page<Task> getAllTasks(Pageable pageable);

    Page<Task> getTasksByCategory(String category, Pageable pageable);

    Page<Task> getTasksByStatus(String status, Pageable pageable);

    Page<Task> getTasksByPriority(int priority, Pageable pageable);

    Task updateTaskById(UUID id, TaskRequest request);

    Task deleteTaskById(UUID id);

    Task assignTask(UUID id, String users);

    Task completeTask(UUID id);

    Task processTask(UUID id, String category);

    
}
