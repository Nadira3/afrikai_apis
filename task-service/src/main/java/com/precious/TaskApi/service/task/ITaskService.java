package com.precious.TaskApi.service.task;

import java.util.UUID;

import com.precious.TaskApi.dto.task.TaskRequest;
import com.precious.TaskApi.model.task.Task;
import com.precious.TaskApi.dto.DataImportResponse;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ITaskService {
    ResponseEntity<DataImportResponse> sendFileToLabelService(UUID taskId, String clientId, String filePath) throws IOException;

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

    Task processTask(UUID taskId);

    
}
