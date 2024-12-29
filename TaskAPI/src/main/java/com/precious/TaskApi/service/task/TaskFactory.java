package com.precious.TaskApi.service.task;

import org.springframework.stereotype.Component;

import com.precious.TaskApi.dto.task.TaskCreationDto;
import com.precious.TaskApi.exception.InvalidTaskRequestException;
import com.precious.TaskApi.model.task.Task;
import com.precious.TaskApi.service.exam.ExamService;
import com.precious.TaskApi.service.training.TrainingService;
import com.precious.TaskApi.service.worktask.WorkTaskService;

import lombok.RequiredArgsConstructor;

// Task Factory
@Component
@RequiredArgsConstructor
public class TaskFactory {
    private final ExamService examService;
    private final TrainingService trainingService;
    private final WorkTaskService workTaskService;
    
    public Task createTask(TaskCreationDto request) {
        return switch (request.getType()) {
            case EXAM -> examService.createExam(request);
            case TRAINING -> trainingService.createTraining(request);
            case WORK -> workTaskService.createWorkTask(request);
            default -> throw new InvalidTaskRequestException("Invalid task type");
        };
    }
}