package com.precious.LabelAPI.service.strategy;

// CSV Implementation
@Component
@Slf4j
public class CSVImportStrategy extends BaseImportStrategy {
    private static final String[] REQUIRED_HEADERS = {"prompt", "response"};
    private final ObjectMapper objectMapper;

    @Override
    public FileType getSupportedFileType() {
        return FileType.CSV;
    }

    @Override
    public boolean validateFile(MultipartFile file) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            validateFileSize(file);
            
            try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
                String[] headers = reader.readNext();
                if (headers == null) {
                    throw new FileValidationException("CSV file is empty");
                }

                // Validate headers
                List<String> missingHeaders = Arrays.stream(REQUIRED_HEADERS)
                    .filter(required -> !Arrays.asList(headers).contains(required))
                    .collect(Collectors.toList());

                if (!missingHeaders.isEmpty()) {
                    throw new FileValidationException("Missing required headers: " + String.join(", ", missingHeaders));
                }

                // Validate row count
                int rowCount = 0;
                while (reader.readNext() != null && rowCount <= MAX_ROWS) {
                    rowCount++;
                }

                if (rowCount < MIN_ROWS || rowCount > MAX_ROWS) {
                    throw new FileValidationException(
                        String.format("Row count must be between %d and %d", MIN_ROWS, MAX_ROWS)
                    );
                }

                return true;
            }
        } catch (Exception e) {
            logProcessingError("CSV validation failed", e);
            return false;
        } finally {
            sample.stop(meterRegistry.timer("import.validation.time", "type", "csv"));
        }
    }

    @Override
    public List<PromptResponsePair> processImport(MultipartFile file, DataImport dataImport) {
        Timer.Sample sample = Timer.start(meterRegistry);
        List<PromptResponsePair> pairs = new ArrayList<>();
        AtomicInteger processedRows = new AtomicInteger(0);
        AtomicInteger errorRows = new AtomicInteger(0);

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] headers = reader.readNext();
            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerMap.put(headers[i], i);
            }

            String[] row;
            while ((row = reader.readNext()) != null) {
                try {
                    PromptResponsePair pair = new PromptResponsePair();
                    pair.setDataImport(dataImport);
                    pair.setPrompt(row[headerMap.get("prompt")]);
                    pair.setResponse(row[headerMap.get("response")]);
                    pair.setOriginalRowNumber(processedRows.incrementAndGet());
                    pair.setProcessingStatus(ProcessingStatus.PENDING);
                    
                    // Validate row data
                    validatePair(pair);
                    
                    pairs.add(pair);
                } catch (Exception e) {
                    errorRows.incrementAndGet();
                    logProcessingError("Error processing row " + processedRows.get(), e);
                }
            }

            // Record metrics
            recordMetrics("import.processed.rows", processedRows.get());
            recordMetrics("import.error.rows", errorRows.get());

            return pairs;
        } catch (Exception e) {
            logProcessingError("CSV processing failed", e);
            throw new FileProcessingException("Failed to process CSV file: " + e.getMessage());
        } finally {
            sample.stop(meterRegistry.timer("import.processing.time", "type", "csv"));
        }
    }

    private void validatePair(PromptResponsePair pair) {
        if (StringUtils.isBlank(pair.getPrompt())) {
            throw new ValidationException("Prompt cannot be empty");
        }
        if (StringUtils.isBlank(pair.getResponse())) {
            throw new ValidationException("Response cannot be empty");
        }
        if (pair.getPrompt().length() > 4000) {
            throw new ValidationException("Prompt exceeds maximum length of 4000 characters");
        }
        if (pair.getResponse().length() > 8000) {
            throw new ValidationException("Response exceeds maximum length of 8000 characters");
        }
    }
}

