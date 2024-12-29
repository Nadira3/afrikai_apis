package com.precious.TaskApi.model.enums;

public enum NotificationType {
    TRAINING("TRAINING"),
    TASK_ASSIGNMENT("TASK_ASSIGNMENT"),
    TASK_EXPIRATION("TASK_EXPIRATION"),
    TASK_COMPLETION("TASK_COMPLETION"),
    TRAINING_COMPLETION("TRAINING_COMPLETION"),
    EXAM_READY("EXAM_READY"),
    EXAM_RESULT("EXAM_RESULT"),
    RATING_UPDATE("RATING_UPDATE"),
    SYSTEM_ALERT("SYSTEM_ALERT");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}