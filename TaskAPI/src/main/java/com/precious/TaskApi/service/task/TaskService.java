package com.precious.TaskApi.service.task;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.precious.TaskApi.dto.task.TaskCreationDto;
import com.precious.TaskApi.exception.InvalidTaskRequestException;
import com.precious.TaskApi.exception.InvalidTaskTransitionException;
import com.precious.TaskApi.exception.TaskCreationException;
import com.precious.TaskApi.exception.TaskNotFoundException;
import com.precious.TaskApi.exception.TaskPopulationException;
import com.precious.TaskApi.model.enums.TaskCategory;
import com.precious.TaskApi.model.enums.TaskStatus;
import com.precious.TaskApi.model.task.Task;
import com.precious.TaskApi.repository.TaskRepository;
import com.precious.TaskApi.service.NotificationService;
import com.precious.TaskApi.util.TaskStateMachine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Service implementation
@Service
@Slf4j
public class TaskService implements ITaskService {
    private final TaskRepository taskRepository;
    private final NotificationService notificationService;
    private final TaskFactory taskFactory;

    public TaskService(TaskRepository taskRepository, NotificationService notificationService, TaskFactory taskFactory) {
        this.taskRepository = taskRepository;
        this.notificationService = notificationService;
        this.taskFactory = taskFactory;
    }

    private void validateTaskRequest(TaskCreationDto request) {

        if (request.getDeadline() != null && request.getDeadline().isBefore(LocalDateTime.now())) {
            throw new InvalidTaskRequestException("Deadline must be in the future");
        }

        if (!Arrays.asList(TaskCategory.values()).contains(request.getCategory())) {
            throw new InvalidTaskRequestException("Invalid task type");
        }
    }

    @Transactional
    public void transitionTaskStatus(Long taskId, TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        // Check if the transition is valid
        if (!TaskStateMachine.isTransitionValid(task.getStatus(), newStatus)) {
            throw new InvalidTaskTransitionException("Invalid transition from " + task.getStatus() + " to " + newStatus);
        }

        // Update task status
        task.setStatus(newStatus);
    }

    private void populateCommonProperties(Task task, TaskCreationDto request) {
        task.setCreatedBy(request.getCreatedBy());
        task.setClientId(request.getClientId());
        task.setCategory(request.getCategory());
        task.setCreatedAt(LocalDateTime.now());
        task.setDeadline(request.getDeadline());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setReward(request.getReward());
        task.setTitle(request.getTitle());
        task.setType(request.getType());
        task.setDurationPerTask(request.getDurationPerTask());   
    }

    @Transactional
    public Task createTask(TaskCreationDto request) {

        try {
                // Validate task creation request
                validateTaskRequest(request);
                
                // Create task using factory
                Task processedTask = taskFactory.createTask(request);
            
                // Set common properties
                populateCommonProperties(processedTask, request);

                // Save task
                Task savedTask = taskRepository.save(processedTask);
                
                // transition task to created state
                transitionTaskStatus(processedTask.getId(), TaskStatus.CREATED);

                // Notify eligible users
                notificationService.notifyNewTrainingAvailable(savedTask);

                return savedTask;
            } catch (InvalidTaskRequestException e) {
                log.error("Invalid task request", e);
                throw e;
            } catch (TaskPopulationException e) {
                log.error("Error populating task", e);
                throw e;
            } catch (InvalidTaskTransitionException e) {
                log.error("Invalid task transition", e);
                throw e;
            } catch (Exception e) {
                log.error("Error creating task", e);
                throw new TaskCreationException("Error creating task");
            }
    }

    // assign task methods and others to be implemented
    

}