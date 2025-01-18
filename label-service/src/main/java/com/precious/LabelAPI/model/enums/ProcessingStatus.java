package com.precious.LabelAPI.model.enums;

/**
 * Enum for processing status of individual prompt-response pairs
 */
public enum ProcessingStatus {
    PENDING,
    LABELED,
    SKIPPED,
    REVIEWED,
    ERROR
}
