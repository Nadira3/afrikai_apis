package com.precious.TaskApi.service.task;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.precious.TaskApi.dto.task.TaskCreationDto;
import com.precious.TaskApi.dto.task.TaskCreationResponseDto;
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

		// Save the task
		TaskResonseDto savedTask = taskRepository.save(task)
			.map(Task::toTaskResponseDto);

		// Update the task status
		transitionTaskStatus(task.getId(), TaskStatus.CREATED);

		// Notify an admin
		notificationService.notifyAdmin(task);

		return response;
	} catch (Exception e) {
	    log.error("Error creating task: " + e.getMessage());
	    throw new TaskCreationException("Error creating task");
	}

    }

// process each task file and save it to the database

	@Transactional
    public void processTasks(TaskCreationDto request, Task task) {
	try {
		MultipartFile trainingFile = request.getTrainingFile();

		TrainingContent trainingContent = new TrainingContent();
		trainingContent.setInstructions(processTraining(trainingFile, task));

		// instantiante a new training object
		Traing training = new Training();
		training.setTrainingContent(trainingContent);
		training.setTask(task);


		trainingContent.setTask(task);
		
		// Save the training content
		trainingRepository.save(training);

		// set training content to task
		task.setTraining(training);


		ExamContent examContent = new ExamContent();
		examContent.set


	    // Save the task
	    taskRepository.save(task);

	    // Update the task status
	    transitionTaskStatus(task.getId(), TaskStatus.CREATED);

	    // Notify an admin
	    notificationService.notifyAdmin(task);

	} catch (Exception e) {
	    log.error("Error creating task: " + e.getMessage());
	    throw new TaskPopulationException("Error creating task");
	}
    }
    

}
