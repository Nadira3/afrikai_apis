package com.precious.TaskApi.model.enums;

// Task Status Enum
public enum TaskStatus {
    CREATED,    // Task created by admin
    PENDING,     // Task is pending for assignment
    AVAILABLE,   // Task is open for taskers
    ASSIGNED,    // Task assigned to a tasker
    IN_PROGRESS, // Tasker is working on the task
    SUBMITTED,   // Tasker submitted the task
    UNDER_REVIEW,// Client is reviewing the submitted task
    COMPLETED,   // Task successfully completed
    REJECTED,    // Task submission rejected
    CANCELLED,
    FAILED,
    EXPIRED;     // Task expired
}
