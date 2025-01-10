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
@RequestMapping("/api/tasks")
@Slf4j
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    // Endpoint to upload a new task
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping(value = "/upload")
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
        URI location = uriComponentsBuilder.path("/api/tasks/{id}").buildAndExpand(task.getId()).toUri();

        // Return the task and the location if task is created successfully
        return ResponseEntity.created(location).body(TaskResponse.fromEntity(task));
    }

    // Endpoint to get task by id
    @GetMapping("/{id}")
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

    // Endpoint to get task by clientId
    @GetMapping("/client/{clientId}")
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

    // Enpoint to get task by UserId
    // This endpoint is used to get all tasks assigned to a user
    @GetMapping("/users/{userId}")
    public ResponseEntity<Page<TaskResponse>> getTaskByUserId(@PathVariable Long userId, Pageable pageable) {
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
            log.error("Error fetching tasks by category: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Page.empty());
        }
    }

    /**
     * Endpoint to get all tasks
     * This endpoint is used to get all tasks in the system
     * This endpoint is only accessible to admin users
     * The admin user is determined by the role of the user
     */
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
            log.error("Error fetching tasks by category: ", e);
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
            log.error("Error fetching tasks by category: ", e);
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

    // Endpoint to update task by id
    @PostMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTaskById(@PathVariable UUID id, @RequestBody @Valid TaskRequest request) {
        // Use the service layer to update the task by id
        Task task = taskService.updateTaskById(id, request);

        // check if the task is updated
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(TaskResponse.toErrorTemplate("Task not found"));
        }

        // Return the task if updated
        return ResponseEntity.ok(TaskResponse.fromEntity(task));
    }

    // Enpoint to delete task by id
    @PostMapping("/{id}/delete")
    public ResponseEntity<Void> deleteTaskById(@PathVariable UUID id) {
        // Use the service layer to delete the task by id
        // This is a soft delete
        Task task = taskService.deleteTaskById(id);

        // check if the task is deleted
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
    @PostMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResponse> assignTask(@PathVariable UUID id, @RequestParam String users) {
        // Use the service layer to assign the task to the list of users
        Task task = taskService.assignTask(id, users);

        // check if the task is assigned
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(TaskResponse.toErrorTemplate("Task not found"));
        }

        // Return the task if assigned
        return ResponseEntity.ok(TaskResponse.fromEntity(task));
    }

    /**
     * Endpoint to complete task
     * This endpoint is used to complete a task
     * The task is completed by the user who was assigned the task
     * The user is determined by the logged-in user
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(@PathVariable UUID id) {
        // Use the service layer to complete the task
        // The user who is completing the task is determined by the logged-in user
        Task task = taskService.completeTask(id);

        // check if the task is completed
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(TaskResponse.toErrorTemplate("Task not found"));
        }

        // Return the task if completed
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
    @PostMapping("/{taskId}/process")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DataImportResponse> processTask(@PathVariable UUID taskId) {
        // Use the service layer to process the task
        DataImportResponse response = taskService.processTask(taskId);

        // check if the task is processed
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(DataImportResponse.toErrorTemplate("Task processing failed"));
        }

        // Return the task if processed
        return ResponseEntity.ok(response);
    }
}
