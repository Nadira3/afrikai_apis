package com.precious.TaskApi.service;

import com.precious.TaskApi.dto.NotificationRequest;
import com.precious.TaskApi.model.enums.NotificationType;
import com.precious.TaskApi.model.task.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final WebClient notificationApiClient;

    public void notifyTaskExpired(Task task) {
        // Logic to send notification
        String message = "Task '" + task.getTitle() + "' has expired. Please take necessary action.";
        // Send email or any other notification type to users/administrators
        sendEmailToUsers(task, message);
    }

    private void sendEmailToUsers(Task task, String message) {
        // Email sending logic (can be done using Spring's Email support)
        // Example: send an email to the creator or assignee of the task
    }

    public void notifyNewTrainingAvailable(Task task) {
        NotificationRequest notification = new NotificationRequest(
            "New Training Available",
            "A new training is available for task type: " + task.getTitle(),
            NotificationType.TRAINING
        );
        sendNotification(notification);
    }

    public void notifyTrainingCompletion(com.precious.TaskApi.dto.task.TrainingCompleteRequest request) {
        Long trainingId = request.getTrainingId();
        Long userId = request.getUserId();

        NotificationRequest notification = new NotificationRequest(
            "Training Completed",
            "Training: " + trainingId + " has been completed successfully",
            NotificationType.TRAINING_COMPLETION
        );
        sendNotification(notification, userId);
    }

    public void notifyTaskAssignment(Long userId, Long taskId) {
        NotificationRequest notification = new NotificationRequest(
            "Task Assigned",
            "You have been assigned to task: " + taskId,
            NotificationType.TASK_ASSIGNMENT
        );
        sendNotification(notification, userId);
    }

    private void sendNotification(NotificationRequest notification, Long... userIds) {
        notificationApiClient.post()
            .uri("/notifications")
            .bodyValue(notification)
            .retrieve()
            .bodyToMono(Void.class)
            .subscribe();
    }
}
