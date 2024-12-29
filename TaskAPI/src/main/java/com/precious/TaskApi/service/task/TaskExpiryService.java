package com.precious.TaskApi.service.task;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.precious.TaskApi.model.enums.TaskStatus;
import com.precious.TaskApi.model.task.Task;
import com.precious.TaskApi.repository.TaskRepository;
import com.precious.TaskApi.service.NotificationService;

@Service
public class TaskExpiryService {

    @Autowired
    private TaskRepository taskRepository;  // Repository to fetch tasks
    @Autowired
    private NotificationService notificationService;  // Service to notify users

    // This method is executed periodically to check for expired tasks
    @Scheduled(cron = "0 0 * * * *")  // Runs every hour (cron expression)
    public void checkForExpiredTasks() {
        // Get current time
        LocalDateTime currentTime = LocalDateTime.now();

        // Find tasks whose deadline has passed and are still active
        List<Task> tasks = taskRepository.findAllByDeadlineBeforeAndStatusNot(currentTime, TaskStatus.EXPIRED);

        for (Task task : tasks) {
            // If the task is still pending or in progress, expire it
            if (task.getStatus() != TaskStatus.COMPLETED && task.getStatus() != TaskStatus.CANCELLED) {
                task.setStatus(TaskStatus.EXPIRED);
                taskRepository.save(task);  // Save the updated task

                // Send a notification about the task expiration
                notificationService.notifyTaskExpired(task);
            }
        }
    }
}

