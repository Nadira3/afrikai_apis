package com.precious.TaskApi.service.task;

import com.precious.TaskApi.dto.task.TaskRequest;
import com.precious.TaskApi.model.task.Task;
import com.precious.TaskApi.model.enums.TaskCategory;
import com.precious.TaskApi.model.enums.TaskStatus;
import com.precious.TaskApi.repository.TaskRepository;
import com.precious.TaskApi.service.StorageService;
import com.precious.TaskApi.service.task.TaskService;
import com.precious.TaskApi.feign.LabelServiceClient;
import com.precious.TaskApi.dto.DataImportRequest;
import com.precious.TaskApi.dto.DataImportResponse;
import com.precious.TaskApi.exception.StorageException;
import com.precious.TaskApi.exception.TaskProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService implements ITaskService {
    
    private final TaskRepository taskRepository;
    private final StorageService storageService;
    private final LabelServiceClient labelServiceClient;
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    @Override
    public ResponseEntity<DataImportResponse> sendFileToLabelService(UUID taskId, String clientId, String filePath) throws IOException {

	try {
        // Create MultipartFile from the file in parent directory
        File file = new File("upload-dir/" + filePath);
	log.info("File path: {}", file.getAbsolutePath());
	log.info("File name: {}", file.getName());
        FileInputStream input = new FileInputStream(file);
	log.info("Done. input was successful");
        MultipartFile multipartFile = new MockMultipartFile(
            file.getName(),
            file.getName(),
            MediaType.APPLICATION_OCTET_STREAM_VALUE,
            IOUtils.toByteArray(input)
        );
        
	log.info("Done. created multipart file");
        // Create request DTO
        DataImportRequest request = new DataImportRequest(multipartFile, clientId, taskId);
	log.info("Done. created request DTO");
        
        // Send request to Label Service
        return labelServiceClient.importData(request);
	} catch (Exception e) {
		log.error("Error sending file to label service: {}", e.getMessage());
		throw new TaskProcessingException("Error sending file to label service");
	}
    }

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

	    try{
            // Store the uploaded files and get their URLs
            mainFileUrl = storageService.store(request.getMainTaskFile(), task.getId());
	    } catch (Exception e) {
		throw new StorageException("Error saving task file");
	    }

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
    public DataImportResponse processTask(UUID taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
	log.info("Processing task: {} with file: {} ", task.getTitle(), task.getMainFileUrl());

        try {
		if (task == null) {
			// log error
			log.error("Task not found");
			// throw exception
			throw new IllegalArgumentException("Task not found");
		}

	// check if response from sendFileToLabelService is successful, then set status to AVAILABLE, otherwise FAILED
	    ResponseEntity<DataImportResponse> response = sendFileToLabelService(taskId, task.getClientId(), task.getMainFileUrl());
	    if (response.getStatusCode() == HttpStatus.CREATED) {
		task.setStatus(TaskStatus.AVAILABLE);
		task.setImportId(response.getBody().getImportId());
	    } else {
		// log warning before setting importId to null
		task.setStatus(TaskStatus.FAILED);
		log.warn("Failed to process task: {}", taskId);
		task.setImportId(null);
	    }

	    // save updated task
            taskRepository.save(task);

	    // return import response
	    return response.getBody();
        } catch (Exception e) {
            log.error("File processing error: ", e);
	    throw new TaskProcessingException("File processing error");
        }
    }

    @Override
    public void deleteAll() {
        taskRepository.deleteAll();
    }
}
