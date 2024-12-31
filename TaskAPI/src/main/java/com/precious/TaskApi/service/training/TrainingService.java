package com.precious.TaskApi.service.training;

import com.precious.TaskApi.dto.task.TaskCreationDto;
import com.precious.TaskApi.dto.task.TrainingCompleteRequest;
import com.precious.TaskApi.exception.TrainingNotFoundException;
import com.precious.TaskApi.model.task.Training;
import com.precious.TaskApi.model.content.TrainingContent;
import com.precious.TaskApi.model.enums.TaskStatus;
import com.precious.TaskApi.model.content.TaskContent;
import com.precious.TaskApi.model.task.Task;
import com.precious.TaskApi.repository.TrainingRepository;
import com.precious.TaskApi.service.NotificationService;
import com.precious.TaskApi.service.exam.ExamContentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashSet;

// Services
@Service
@Slf4j
public class TrainingService {
    private final TrainingRepository trainingRepository;
    private final NotificationService notificationService;
    private final WebClient taskSpecificApiClient;
    private final ExamContentService examContentService;

    @Autowired
    public TrainingService
    (
        ExamContentService examContentService,
        TrainingRepository trainingRepository, 
        NotificationService notificationService, 
        WebClient taskSpecificApiClient
        ) {
            this.examContentService = examContentService;
        this.trainingRepository = trainingRepository;
        this.notificationService = notificationService;
        this.taskSpecificApiClient = taskSpecificApiClient;
    }

    @Transactional
    public Training createTraining(TaskCreationDto request) {
        // Fetch training content from task-specific API
        TaskContent content = examContentService.createExamContent(request);
        Training training = new Training();
        training.setContent((TrainingContent) content);

        // Save training
        return trainingRepository.save(training);
    }

    @Transactional
    public void completeTraining(TrainingCompleteRequest request) {
        Training training = trainingRepository.findById(request.getTrainingId())
                .map(training1 -> {
                    if (training1.getCompletedAt() != null) {
                        throw new IllegalStateException("Training already completed");
                    }
                    return training1;
                })
                .map(training1 -> {
                    if (request.getCompletedAt().isBefore(training1.getAssignedAt())) {
                        throw new IllegalStateException("Training completed before assigned");
                    }
                    return training1;
                })
                .map(training1 -> {
                    if (request.getCompletedAt().isAfter(training1.getDeadline())) {
                        throw new IllegalStateException("Training completed after deadline");
                    }
                    return training1;
                })
            .orElseThrow(() -> new TrainingNotFoundException(request.getTrainingId()));

        // Update training status
        training.setCompletedAt(request.getCompletedAt());

        // update task status to completed
        training.setStatus(TaskStatus.COMPLETED);

        // Notify users
        notificationService.notifyTrainingCompletion(request);
    }
}
