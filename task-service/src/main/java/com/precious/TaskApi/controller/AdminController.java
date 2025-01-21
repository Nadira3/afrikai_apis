package com.precious.TaskApi.controller;

import java.net.URI;
import java.util.UUID;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
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
import com.precious.TaskApi.model.task.Task;
import com.precious.TaskApi.service.task.TaskService;
import com.precious.TaskApi.exception.TaskNotFoundException;
import com.precious.TaskApi.exception.TaskProcessingException;
import com.precious.TaskApi.exception.UnauthorizedException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/tasks/admin")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Endpoints for admin users")
public class AdminController {
    private final TaskService taskService;
    /**
     * Endpoint to get all tasks
     * This endpoint is used to get all tasks in the system
     * This endpoint is only accessible to admin users
     * The admin user is determined by the role of the user
     */

    @Operation(summary = "Get all tasks", 
        description = "Get all tasks in the system",
	responses = {
	    @ApiResponse(responseCode = "200", description = "Tasks fetched successfully",
	    		content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)))
	    ),
	    @ApiResponse(responseCode = "404", description = "No tasks found")
	}
    )
    @GetMapping({"", "/"})
    public ResponseEntity<Page<TaskResponse>> getAllTasks(Pageable pageable) {
        Page<TaskResponse> tasksPage = taskService.getAllTasks(pageable)
                .map(TaskResponse::fromEntity);
        // Check if no tasks are found
        if (tasksPage == null || tasksPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Page.empty());
        }
        return ResponseEntity.ok(tasksPage);
    }

    /**
     * Endpoint to get all tasks by category
     * This endpoint is used to get all tasks in the system by category
     * This endpoint is only accessible to admin users
     * The admin user is determined by the role of the user
     * The category is passed as a query parameter
     * The category is used to filter the tasks
     */

    @Operation(summary = "Get tasks by category", 
	description = "Get all tasks in the system by category",
	parameters = {
	    @Parameter(name = "category", description = "Category of the task", required = true)
	},
	responses = {
	    @ApiResponse(responseCode = "200", description = "Tasks fetched successfully",
	    		content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)))
	    ),
	    @ApiResponse(responseCode = "404", description = "No tasks found")
	}
    )
    @GetMapping("/category")
    public ResponseEntity<Page<TaskResponse>> getTasksByCategory(@RequestParam String category, Pageable pageable) {
        try {
            // Fetch paginated tasks by category
            Page<Task> tasksPage = taskService.getTasksByCategory(category, pageable);

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Page.empty());
        }
    }

    /**
     * Endpoint to get all tasks by status
     * This endpoint is used to get all tasks in the system by status
     * This endpoint is only accessible to admin users
     * The admin user is determined by the role of the user
     * The status is passed as a query parameter
     * The status is used to filter the tasks
     */

    @Operation(summary = "Get tasks by status",
    	description = "Get all tasks in the system by status",
	parameters = {
	    @Parameter(name = "status", description = "Status of the task", required = true)
	},
	responses = {
	    @ApiResponse(responseCode = "200", description = "Tasks fetched successfully",
	    		content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)))
	    ),
	    @ApiResponse(responseCode = "404", description = "No tasks found")
	}
    )
    @GetMapping("/status")
    public ResponseEntity<Page<TaskResponse>> getTasksByStatus(@RequestParam String status, Pageable pageable) {
        try {
            // Fetch paginated tasks by category
            Page<Task> tasksPage = taskService.getTasksByStatus(status, pageable);

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Page.empty());
        }
    }

    /**
     * Endpoint to get all tasks by priority
     * This endpoint is used to get all tasks in the system by priority
     * This endpoint is only accessible to admin users
     * The admin user is determined by the role of the user
     * The priority is passed as a query parameter
     * The priority is used to filter the tasks
     */

    @Operation(summary = "Get tasks by priority",
    	description = "Get all tasks in the system by priority",
	parameters = {
	    @Parameter(name = "priority", description = "Priority of the task", required = true)
	},
	responses = {
	    @ApiResponse(responseCode = "200", description = "Tasks fetched successfully",
	    		content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)))
	    ),
	    @ApiResponse(responseCode = "404", description = "No tasks found")
	}
    )
    @GetMapping("/priority")
    public ResponseEntity<Page<TaskResponse>> getTasksByPriority(@RequestParam Integer priority, Pageable pageable) {
        Page<TaskResponse> tasksPage = taskService.getTasksByPriority(priority, pageable)
                .map(TaskResponse::fromEntity);
        // Check if no tasks are found
        if (tasksPage == null || tasksPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Page.empty());
        }
        return ResponseEntity.ok(tasksPage);
    }

    // Enpoint to delete task by id
    @Operation(summary = "Delete task by id",
    	description = "Delete a task by id",
	parameters = {
	    @Parameter(name = "taskId", description = "Id of the task", required = true)
	},
	responses = {
	    @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
	    @ApiResponse(responseCode = "401", description = "Unauthorized to delete task")
	}
    )
    @PostMapping("/{taskId}/delete")
    public ResponseEntity<Void> deleteTaskById(@PathVariable UUID taskId) {
        // Use the service layer to delete the task by id
        // This is a soft delete
        Task task = taskService.deleteTaskById(taskId);

        // check if the task is deleted
        if (task == null) {
		throw new UnauthorizedException("You are not authorized to delete this task");
        }
        return ResponseEntity.noContent().build();

    }

    /**
     * Endpoint to assign task to a list of users
     * This endpoint is used to assign a task to a list of users
     * The list of users is passed as a request parameter
     * The list of users is a comma-separated list of user ids
     * The task is assigned to each user in the list
     */

    @Operation(summary = "Assign task to users",
    	description = "Assign a task to a list of users",
	parameters = {
	    @Parameter(name = "taskId", description = "Id of the task", required = true),
	    @Parameter(name = "users", description = "List of user ids", required = true)
	},
	responses = {
	    @ApiResponse(responseCode = "200", description = "Task assigned successfully",
	    		content = @Content(schema = @Schema(implementation = TaskResponse.class))
	    ),
	    @ApiResponse(responseCode = "404", description = "Task not found")
	}
    )
    @PostMapping("/{taskId}/assign")
    public ResponseEntity<TaskResponse> assignTask(@PathVariable UUID taskId, @RequestParam String users) {
        // Use the service layer to assign the task to the list of users
        Task task = taskService.assignTask(taskId, users);

        // check if the task is assigned
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(TaskResponse.toErrorTemplate("Task not found"));
        }

        // Return the task if assigned
        return ResponseEntity.ok(TaskResponse.fromEntity(task));
    }

    /**
     * Endpoint to send tasks for processing before assignment
     * This endpoint is used to send tasks for processing before assignment
     * The task is sent for processing by the logged-in admin user
     *
     * The admin user is determined by the role of the user
     * The processing queue is determined by the category of the task
     * The category is passed as a request parameter
     * The task is sent to the processing queue of the corresponding category
     */

    @Operation(summary = "Send task for processing",
    	description = "Send a task for processing before assignment",
	parameters = {
		@Parameter(name = "taskId", description = "Id of the task", required = true)
	},
	responses = {
	    @ApiResponse(responseCode = "200", description = "Task sent for processing successfully",
	    		content = @Content(schema = @Schema(implementation = DataImportResponse.class))
	    ),
	    @ApiResponse(responseCode = "404", description = "Task not found"),
	    @ApiResponse(responseCode = "401", description = "Task processing failed")
	}
    )
    @PostMapping("/{taskId}/process")
    public ResponseEntity<DataImportResponse> processTask(@PathVariable UUID taskId) {
        // Use the service layer to process the task
	try {
		DataImportResponse response = taskService.processTask(taskId);

        // check if the task is processed
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(DataImportResponse.toErrorTemplate("Task processing failed"));
        }

        // Return the task if processed
        return ResponseEntity.ok(response);
	} catch (Exception e) {
		throw new TaskProcessingException("Error processing task");
	}
    }

    /**
     * Endpoint to complete task
     * This endpoint is used to complete a task
     * The task is completed by the user who was assigned the task
     * The user is determined by the logged-in user
     */

    @Operation(summary = "Complete task",
        description = "mark a task as completed",
	parameters = {
	    @Parameter(name = "taskId", description = "Id of the task", required = true)
	},
	responses = {
	    @ApiResponse(responseCode = "200", description = "Task completed successfully",
	    		content = @Content(schema = @Schema(implementation = TaskResponse.class))
	    ),
	    @ApiResponse(responseCode = "404", description = "Task not found")
	}
    )
    @PostMapping("/{taskId}/complete")
    public ResponseEntity<TaskResponse> completeTask(@PathVariable UUID taskId) {
        // Use the service layer to complete the task
        // The user who is completing the task is determined by the logged-in user
        Task task = taskService.completeTask(taskId);

        // check if the task is completed
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(TaskResponse.toErrorTemplate("Task not found"));
        }

        // Return the task if completed
        return ResponseEntity.ok(TaskResponse.fromEntity(task));
    }
}
