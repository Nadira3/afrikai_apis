package com.precious.TaskApi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;

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
import com.precious.TaskApi.exception.TaskNotFoundException;
import com.precious.TaskApi.model.task.Task;
import com.precious.TaskApi.service.task.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/tasks/user")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "User", description = "Endpoints for user operations")
public class UserController {
    private final TaskService taskService;

    // Endpoint to get task by UserId
    // This endpoint is used to get all tasks assigned to a user
    @GetMapping("/{userId}")
    @Operation(summary = "Get tasks by userId",
        description = "Get all tasks assigned to a user by userId",
	parameters = {
	    @Parameter(name = "userId", description = "The id of the user to get tasks for", in = ParameterIn.PATH, required = true)
	},
	responses = {
	    @ApiResponse(responseCode = "200", description = "Tasks found", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)))),
	    @ApiResponse(responseCode = "404", description = "Tasks not found", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class))))
	}
    )
    public ResponseEntity<Page<TaskResponse>> getTasksByUserId(@PathVariable Long userId, Pageable pageable) {
        try {
            // Fetch paginated tasks by category
            Page<Task> tasksPage = taskService.getTaskByUserId(userId, pageable);

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
	    throw new TaskNotFoundException("No tasks found with this userId: " + userId);
        }
    }
}
