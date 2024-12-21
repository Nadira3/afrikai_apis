package com.precious.LabelAPI.service.strategy;

import io.micrometer.core.instrument.MeterRegistry;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;


// Abstract base strategy with common functionality
@Slf4j
public abstract class BaseImportStrategy implements DataImportStrategy {

    @Autowired
    protected MeterRegistry meterRegistry;

    // Configurable file and data size limits
    protected static final int MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    protected static final int MIN_ROWS = 1;
    protected static final int MAX_ROWS = 10000;

    /**
     * Validates the file size to ensure it does not exceed the maximum limit.
     * Throws FileValidationException if the file size exceeds the limit.
     * Logs success if validation passes.
     * 
     * @param file the file to validate
     */
    protected void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileValidationException("File size exceeds maximum limit of 10MB");
        }
        log.info("File size validation passed for file: {}", file.getOriginalFilename());
    }

    /**
     * Records custom metrics using the MeterRegistry for performance tracking.
     * Logs a warning if value is negative.
     * 
     * @param metricName the name of the metric
     * @param value the value to record
     * @param tags additional tags for categorization
     */
    protected void recordMetrics(String metricName, long value, String... tags) {
        if (value < 0) {
            log.warn("Attempted to record a negative metric value: {} for {}", value, metricName);
            return;
        }
        meterRegistry.counter(metricName, tags).increment(value);
    }

    /**
     * Logs processing errors and records them as metrics for monitoring purposes.
     * Includes additional details like the file name for better debugging.
     * 
     * @param message a custom error message
     * @param e the exception that occurred
     */
    protected void logProcessingError(String message, Exception e) {
        log.error("Processing error: {} - {}", message, e.getMessage());
        recordMetrics("import.errors", 1, "type", e.getClass().getSimpleName());
    }
}
