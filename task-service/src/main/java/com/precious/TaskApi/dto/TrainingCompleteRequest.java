package com.precious.TaskApi.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TrainingCompleteRequest {
    private Long trainingId;
    private Long userId;
    private LocalDateTime completedAt;
}
