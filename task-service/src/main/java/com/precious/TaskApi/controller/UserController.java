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
@RequestMapping("/api/user/tasks")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final TaskService taskService;

    // Endpoint to get task by UserId
    // This endpoint is used to get all tasks assigned to a user
    @GetMapping("/{userId}")
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
            log.error("Error fetching tasks by category: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Page.empty());
        }
    }
}
