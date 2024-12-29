package com.precious.TaskApi.dto.task;

import lombok.Data;

import java.util.Map;

@Data
public class ExamSubmissionRequest {
    private Long userId;
    private Map<String, String> answers;
}
