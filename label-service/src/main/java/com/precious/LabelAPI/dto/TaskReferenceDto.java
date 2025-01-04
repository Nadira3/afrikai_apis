package com.precious.LabelAPI.dto;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * DTO for Task entity references from external service
 * Records automatically provide:
 * - Constructor
 * - Getters
 * - equals()/hashCode()
 * - toString()
 */
public record TaskReferenceDto(
    UUID id,
    String type,
    String status,
    LocalDateTime createdAt
) {
    public TaskReferenceDto {
        Objects.requireNonNull(id, "Task ID cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
}
