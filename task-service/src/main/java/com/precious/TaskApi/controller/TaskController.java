package com.precious.TaskApi.controller;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpStatus;


import java.net.URI;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import com.precious.TaskApi.dto.task.TaskCreationDto;
import com.precious.TaskApi.dto.task.TaskResponseDto;
import com.precious.TaskApi.model.enums.ImportStatus;
import com.precious.TaskApi.model.enums.TaskCategory;
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
            @Valid @ModelAttribute TaskCreationDto taskCreationDto,
            @AuthenticationPrincipal Principal principal,
            UriComponentsBuilder uriComponentsBuilder) {

        if (principal == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new TaskResponseDto());
            }

        // Extract the logged-in user
        String clientId = principal.getName(); // Assumes username is the clientId

        // Use the service layer to create the task
        Task task = taskService.createTask(taskCreationDto, clientId);

        // check if the task is created successfully
        if (task == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(TaskResponseDto.toErrorTemplate(taskCreationDto));
        }

        // Build the URI for the newly created task
        URI location = uriComponentsBuilder.path("/api/tasks/{id}").buildAndExpand(task.getId()).toUri();

        // Return the task and the location if task is created successfully
        return ResponseEntity.created(location).body(task.toTaskResponseDto());
    }

}
