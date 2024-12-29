package com.precious.TaskApi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import com.precious.TaskApi.dto.task.TaskCreationDto;
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

    @PostMapping("/create")
    public ResponseEntity<Task> createTask(@RequestBody TaskCreationDto request, UriComponentsBuilder ucb) {
        URI locationOfNewTask = ucb.path("/api/tasks/{id}")
                                    .buildAndExpand(taskService.createTask(request).getId())
                                    .toUri();
        return ResponseEntity.created(locationOfNewTask).build();
    }

    // @PostMapping("/{taskId}/review")
    // public ResponseEntity<Review> reviewTask(
    //         @PathVariable Long taskId,
    //         @RequestBody TaskReviewRequest request) {
    //     return ResponseEntity.ok(taskService.reviewTask(taskId, request));
    // }

    // @GetMapping("/qualified")
    // public ResponseEntity<List<Task>> getQualifiedTasks(@RequestParam Long userId) {
    //     return ResponseEntity.ok(taskService.findQualifiedTasksForUser(userId));
    // }
}
