package com.precious.TaskApi.dto.task;

import lombok.Data;

@Data
public class TaskReviewRequest {
    private Double rating;
    private String feedback;
    private Long reviewedBy;
}
