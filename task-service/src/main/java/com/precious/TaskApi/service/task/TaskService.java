package com.precious.TaskApi.service.task;

import com.precious.TaskApi.dto.task.TaskRequest;
import com.precious.TaskApi.model.task.Task;
import com.precious.TaskApi.model.enums.TaskCategory;
import com.precious.TaskApi.model.enums.TaskStatus;
import com.precious.TaskApi.repository.TaskRepository;
import com.precious.TaskApi.service.StorageService;
import com.precious.TaskApi.service.task.TaskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService implements ITaskService {
    
    private final TaskRepository taskRepository;
    private final StorageService storageService;

    @Override
    @Transactional
    public Task createTask(TaskRequest request, String clientId) {
        try {
            String mainFileUrl = null;

            // Create new task entity
            Task task = Task.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .reward(request.getReward())
                    .category(request.getCategory())
                    .deadline(request.getDeadline())
                    .clientId(clientId)
                    .status(TaskStatus.PENDING)
                    .createdAt(request.getCreatedAt())
                    .build();

            // Store the uploaded files and get their URLs
            mainFileUrl = storageService.store(request.getMainTaskFile(), task.getId());

            // set main file URL
            task.setMainFileUrl(mainFileUrl);

            return taskRepository.save(task);
        } catch (Exception e) {
            log.error("Error creating task: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Task getTaskById(UUID id) {
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Task> getTaskByClientId(String clientId, Pageable pageable) {
        return taskRepository.findAllByClientId(clientId, pageable);
    }

    @Override
    public Page<Task> getTaskByUserId(Long userId, Pageable pageable) {
        return taskRepository.findByAssignedUserId(userId, pageable);
    }

    @Override
    public Page<Task> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    @Override
    public Page<Task> getTasksByCategory(String category, Pageable pageable) {
        try {
            TaskCategory taskCategory = TaskCategory.valueOf(category.toUpperCase());
            return taskRepository.findByCategory(taskCategory, pageable);
        } catch (IllegalArgumentException e) {
            log.error("Invalid category: {}", category);
            return null;
        }
    }

    @Override
    public Page<Task> getTasksByStatus(String status, Pageable pageable) {
        try {
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            return taskRepository.findByStatus(taskStatus, pageable);
        } catch (IllegalArgumentException e) {
            log.error("Invalid status: {}", status);
            return null;
        }
    }

    @Override
    public Page<Task> getTasksByPriority(int priority, Pageable pageable) {
        return taskRepository.findByPriority(priority, pageable);
    }

    @Override
    @Transactional
    public Task updateTaskById(UUID id, TaskRequest request) {
        Task existingTask = taskRepository.findById(id).orElse(null);
        if (existingTask == null) {
            return null;
        }

        try {
            // Update file URLs if new files are provided
            if (request.getMainTaskFile() != null) {
                String mainFileUrl = storageService.store(request.getMainTaskFile(), id);
                existingTask.setMainFileUrl(mainFileUrl);
            }

            // Update other fields
            existingTask.setTitle(request.getTitle());
            existingTask.setDescription(request.getDescription());
            existingTask.setReward(request.getReward());
            existingTask.setCategory(request.getCategory());
            existingTask.setDeadline(request.getDeadline());

            return taskRepository.save(existingTask);
        } catch (Exception e) {
            log.error("Error updating task: {}", e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public Task deleteTaskById(UUID id) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            task.setStatus(TaskStatus.COMPLETED);
            return taskRepository.save(task);
        }
        return null;
    }

    @Override
    @Transactional
    public Task assignTask(UUID id, String users) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task == null) {
            return null;
        }

        try {
            List<Long> userIds = Arrays.stream(users.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            task.setAssignedUserIds(userIds);
            task.setStatus(TaskStatus.ASSIGNED);
            return taskRepository.save(task);
        } catch (Exception e) {
            log.error("Error assigning task: {}", e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public Task completeTask(UUID id) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            task.setStatus(TaskStatus.COMPLETED);
            return taskRepository.save(task);
        }
        return null;
    }

    @Override
    @Transactional
    public Task processTask(UUID id, String category) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task == null) {
            return null;
        }

        try {
            TaskCategory taskCategory = TaskCategory.valueOf(category.toUpperCase());
            task.setCategory(taskCategory);
            task.setStatus(TaskStatus.AVAILABLE);
            return taskRepository.save(task);
        } catch (IllegalArgumentException e) {
            log.error("Invalid category: {}", category);
            return null;
        }
    }

    @Override
    public void deleteAll() {
        taskRepository.deleteAll();
    }
}