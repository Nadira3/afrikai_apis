package com.precious.LabelAPI.service.strategy;

// Abstract base strategy with common functionality
@Slf4j
public abstract class BaseImportStrategy implements DataImportStrategy {
    @Autowired
    protected MeterRegistry meterRegistry;
    
    protected static final int MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    protected static final int MIN_ROWS = 1;
    protected static final int MAX_ROWS = 10000;

    protected void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileValidationException("File size exceeds maximum limit of 10MB");
        }
    }

    protected void recordMetrics(String metricName, long value, String... tags) {
        meterRegistry.counter(metricName, tags).increment(value);
    }

    protected void logProcessingError(String message, Exception e) {
        log.error("Processing error: {} - {}", message, e.getMessage());
        recordMetrics("import.errors", 1, "type", e.getClass().getSimpleName());
    }
}
