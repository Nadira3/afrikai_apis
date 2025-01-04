package com.precious.TaskApi.dto.task;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TrainingCompleteRequest {
    private Long userId;
    private LocalDateTime completedAt;
    public Long getTrainingId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTrainingId'");
    }
}
