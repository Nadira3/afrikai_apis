package com.precious.TaskApi.controller;

import java.net.URI;
import java.util.UUID;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;


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
import com.precious.TaskApi.exception.TaskNotFoundException;
import com.precious.TaskApi.exception.StorageException;
import com.precious.TaskApi.model.task.Task;
import com.precious.TaskApi.service.task.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/tasks/client")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Client", description = "Endpoints for client operations")
public class ClientController {
    private final TaskService taskService;

    // Endpoint to upload a new task
    @PostMapping("/upload")
    @Operation(summary = "Upload a new task",
        description = "Upload a new task for a client",

	requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskRequest.class))),
	responses = {
	    @ApiResponse(responseCode = "201", description = "Task created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class))),
	    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class))),
	    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class)))
	}
    )
    public ResponseEntity<TaskResponse> uploadTask(
            @ModelAttribute @Valid TaskRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            UriComponentsBuilder uriComponentsBuilder) {

	    try {
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
	    } catch (Exception e) {
		    throw new StorageException("Unauthorized access");
	}
    }


    // Endpoint to get task by clientId
    @GetMapping("/{clientId}")
    @Operation(summary = "Get tasks by clientId",
	description = "Get tasks by clientId",
	parameters = {
	    @Parameter(name = "clientId", description = "The clientId of the tasks to fetch", required = true, in = ParameterIn.PATH)
	},
	responses = {
	    @ApiResponse(responseCode = "200", description = "Tasks fetched successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
	    @ApiResponse(responseCode = "404", description = "No tasks found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
	    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
	}
    )
    public ResponseEntity<Page<TaskResponse>> getTaskByClientId(@PathVariable String clientId, Pageable pageable) {
        try {
            // Fetch paginated tasks by category
            Page<Task> tasksPage = taskService.getTaskByClientId(clientId, pageable);

            // Check if no tasks are found
            if (tasksPage == null || tasksPage.isEmpty()) {
		throw new TaskNotFoundException("No tasks found with this clientId: " + clientId);
            }

            // Convert the page of tasks into a page of TaskResponse
            Page<TaskResponse> taskResponsesPage = tasksPage.map(TaskResponse::fromEntity);

            // Return paginated response
            return ResponseEntity.ok(taskResponsesPage);
        } catch (Exception e) {
            // Handle any unexpected exceptions and return a proper error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Page.empty());
        }
    }

    // Endpoint to update task by id
    @PostMapping("/{taskId}")
    @Operation(summary = "Update task by id",
      	description = "Update task by id",
	requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskRequest.class))),
	parameters = {
	    @Parameter(name = "taskId", description = "The id of the task to update", required = true, in = ParameterIn.PATH)
	},
	responses = {
	    @ApiResponse(responseCode = "200", description = "Task updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class))),
	    @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class))),
	    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class)))
	}
    )
    public ResponseEntity<TaskResponse> updateTaskById(@PathVariable UUID taskId, @RequestBody @Valid TaskRequest request) {
	try {
        // Use the service layer to update the task by id
        Task task = taskService.updateTaskById(taskId, request);

        // check if the task is updated
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(TaskResponse.toErrorTemplate("Task not found"));
        }

        // Return the task if updated
        return ResponseEntity.ok(TaskResponse.fromEntity(task));
	} catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(TaskResponse.toErrorTemplate("Task not found"));
	}
    }
}
