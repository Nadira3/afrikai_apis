package com.precious.TaskApi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;

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
@RequestMapping("/api/tasks")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "General", description = "Endpoints for task operations")
public class TaskController {
    private final TaskService taskService;

    // Endpoint to get task by id
    @GetMapping("/{id}")

    @Operation(summary = "Get task by id",
	description = "Get a task by id",
	responses = {
	    @ApiResponse(responseCode = "200", description = "Task found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class))),
	    @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class)))
	}
    )
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable UUID id) {
        // Use the service layer to get the task by id
        Task task = taskService.getTaskById(id);

        // check if the task is found
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(TaskResponse.toErrorTemplate("Task not found"));
        }

        // Return the task if found
        return ResponseEntity.ok(TaskResponse.fromEntity(task));
    }
}
