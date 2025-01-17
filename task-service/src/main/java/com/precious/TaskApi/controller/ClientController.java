package com.precious.TaskApi.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.precious.TaskApi.dto.task.TaskRequest;
import com.precious.TaskApi.dto.task.TaskResponse;
import com.precious.TaskApi.dto.DataImportResponse;
import com.precious.TaskApi.model.task.Task;
import com.precious.TaskApi.service.task.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/tasks/client")
@Slf4j
@RequiredArgsConstructor
public class ClientController {
    private final TaskService taskService;

    // Endpoint to upload a new task
    @PostMapping("/upload")
    public ResponseEntity<TaskResponse> uploadTask(
            @ModelAttribute @Valid TaskRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            UriComponentsBuilder uriComponentsBuilder) {

        // Extract the logged-in user
        String clientId = userDetails.getUsername(); // Assumes username is the clientId

        // Use the service layer to create the task
        Task task = taskService.createTask(request, clientId);

        // check if the task is created successfully
        if (task == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(TaskResponse.toErrorTemplate(request));
        }

        // Build the URI for the newly created task
        URI location = uriComponentsBuilder.path("/api/tasks/client/{id}").buildAndExpand(task.getId()).toUri();

        // Return the task and the location if task is created successfully
        return ResponseEntity.created(location).body(TaskResponse.fromEntity(task));
    }


    // Endpoint to get task by clientId
    @GetMapping("/{clientId}")
    public ResponseEntity<Page<TaskResponse>> getTaskByClientId(@PathVariable String clientId, Pageable pageable) {
        try {
            // Fetch paginated tasks by category
            Page<Task> tasksPage = taskService.getTaskByClientId(clientId, pageable);

            // Check if no tasks are found
            if (tasksPage == null || tasksPage.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Page.empty());
            }

            // Convert the page of tasks into a page of TaskResponse
            Page<TaskResponse> taskResponsesPage = tasksPage.map(TaskResponse::fromEntity);

            // Return paginated response
            return ResponseEntity.ok(taskResponsesPage);
        } catch (Exception e) {
            // Handle any unexpected exceptions and return a proper error message
            log.error("Error fetching tasks by category: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Page.empty());
        }
    }

    // Endpoint to update task by id
    @PostMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTaskById(@PathVariable UUID taskId, @RequestBody @Valid TaskRequest request) {
        // Use the service layer to update the task by id
        Task task = taskService.updateTaskById(taskId, request);

        // check if the task is updated
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(TaskResponse.toErrorTemplate("Task not found"));
        }

        // Return the task if updated
        return ResponseEntity.ok(TaskResponse.fromEntity(task));
    }
}
