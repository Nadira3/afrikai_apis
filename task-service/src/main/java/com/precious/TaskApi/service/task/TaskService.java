package com.precious.TaskApi.service.task;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.precious.TaskApi.dto.task.TaskCreationDto;
import com.precious.TaskApi.exception.InvalidTaskRequestException;
import com.precious.TaskApi.exception.InvalidTaskTransitionException;
import com.precious.TaskApi.exception.TaskCreationException;
import com.precious.TaskApi.exception.TaskNotFoundException;
import com.precious.TaskApi.model.enums.TaskCategory;
import com.precious.TaskApi.model.enums.TaskStatus;
import com.precious.TaskApi.model.task.Task;
import com.precious.TaskApi.repository.TaskRepository;
import com.precious.TaskApi.service.NotificationService;
import com.precious.TaskApi.service.web.ExternalEntityService;
import com.precious.TaskApi.util.TaskStateMachine;
import com.precious.TaskApi.model.QueryResponse;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

// Service implementation
@Service
@Slf4j
public class TaskService implements ITaskService {
	private final TaskRepository taskRepository;
	private final NotificationService notificationService;
	private final ExternalEntityService externalEntityService;

	public TaskService(TaskRepository taskRepository, NotificationService notificationService, ExternalEntityService externalEntityService) {
		this.taskRepository = taskRepository;
		this.notificationService = notificationService;
		this.externalEntityService = externalEntityService;

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
	public void transitionTaskStatus(UUID taskId, TaskStatus newStatus) {
		Task task = taskRepository.findById(taskId)
				.orElseThrow(() -> new TaskNotFoundException("Task not found"));

		// Check if the transition is valid
		if (!TaskStateMachine.isTransitionValid(task.getStatus(), newStatus)) {
			throw new InvalidTaskTransitionException(
					"Invalid transition from " + task.getStatus() + " to " + newStatus);
		}

		// Update task status
		task.setStatus(newStatus);
	}

	@Transactional
	public Task createTask(TaskCreationDto request, String clientId) {

		try {
			// Validate task creation request
			validateTaskRequest(request);

			// Create the task
			Task task = new Task();
			task.setClientId(clientId);
			task.setTitle(request.getTitle());
			task.setDescription(request.getDescription());
			task.setReward(request.getReward());
			task.setCreatedAt(request.getCreatedAt());
			task.setDeadline(request.getDeadline());
			task.setCategory(request.getCategory());

			// Update the task status
			transitionTaskStatus(task.getId(), TaskStatus.PENDING);

			// Notify an admin
			// notificationService.notifyAdmin(task);

			// send the file for processing
			return sendFileForProcessing(request, UUID.fromString(clientId), task)
					.map(response -> {
						// Save the task
						return taskRepository.save(task);
					})
					.block();
		} catch (Exception e) {
			log.error("Error creating task: " + e.getMessage());
			throw new TaskCreationException("Error creating task");
		}

	}

	 public Mono<String> sendFileForProcessing(TaskCreationDto taskDto, UUID clientId, Task task) {
        // Call ExternalEntityService to process the file
        return externalEntityService.uploadAndProcessFile(taskDto.getCategory().toString(), taskDto.getFile(), clientId)
                .flatMap(response -> {
                    // Handle the response from LabelAPI
                    System.out.println("Task created and file processed: " + response);
					task.setImportId(response.getImportId());
                    return Mono.just("Task created successfully with response: " + response);
                })
                .onErrorResume(error -> {
                    System.err.println("Error creating task: " + error.getMessage());
                    return Mono.error(new RuntimeException("Task creation failed"));
                });
    }

}
