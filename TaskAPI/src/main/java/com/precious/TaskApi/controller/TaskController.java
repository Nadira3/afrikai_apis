package com.precious.TaskApi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.validation.BindingResult;
import org.springframework.http.HttpStatus;
import java.util.stream.Collectors;

import java.net.URI;
import java.security.Principal;
import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import com.precious.TaskApi.dto.task.TaskCreationDto;
import com.precious.TaskApi.dto.task.TaskResponseDto;
import com.precious.TaskApi.dto.task.TaskProcessingDto;
import com.precious.TaskApi.dto.task.TaskReviewRequest;
import com.precious.TaskApi.model.Review;
import com.precious.TaskApi.model.task.Task;
import com.precious.TaskApi.service.task.TaskService;

@RestController
@RequestMapping("/api/tasks")
@Slf4j
public class TaskController {
	private final TaskService taskService;

    TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Endpoint to upload a new task
    @PostMapping("/upload")
    public ResponseEntity<TaskResponseDto> uploadTask(
        @RequestBody @Valid TaskCreationDto taskCreationDto, 
        @AuthenticationPrincipal Principal principal,
        UriComponentsBuilder uriComponentsBuilder
        ) {

        // Extract the logged-in user
        String clientId = principal.getName(); // Assumes username is the clientId
        
        // Use the service layer to create the task
        Task task = taskService.createTask(taskCreationDto, clientId);

        // check if the task is created successfully
        if (task == null) {
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(TaskResponseDto.toErrorTemplate(taskCreationDto));
	}

        // Build the URI for the newly created task
        URI location = uriComponentsBuilder.path("/api/tasks/{id}").buildAndExpand(task.getId()).toUri();

        // Return the task and the location if task is created successfully
        return ResponseEntity.created(location).body(TaskResponseDto.fromEntity(task));
    }

}
