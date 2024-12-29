package com.precious.TaskApi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.precious.TaskApi.model.enums.NotificationPriority;
import com.precious.TaskApi.model.enums.NotificationType;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String title;
    private String message;
    private NotificationType type;
    private Map<String, Object> metadata;
    private NotificationPriority priority;
    private List<Long> recipientIds;
    private LocalDateTime scheduledFor;
    
    // Constructor used in the current NotificationService
    public NotificationRequest(String title, String message, NotificationType type) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.priority = NotificationPriority.NORMAL;
        this.scheduledFor = LocalDateTime.now();
    }
}