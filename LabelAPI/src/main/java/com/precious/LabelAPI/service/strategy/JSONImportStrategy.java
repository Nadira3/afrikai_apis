package com.precious.LabelAPI.service.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.precious.LabelAPI.model.DataImport;
import com.precious.LabelAPI.model.PromptResponsePair;
import com.precious.LabelAPI.model.ProcessingStatus;
import com.precious.LabelAPI.service.exception.FileProcessingException;
import com.precious.LabelAPI.service.exception.FileValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
* JSON Import Strategy Implementation
* This class is responsible for handling JSON file imports.
* It validates the file and processes the data entries.
* It also records metrics for validation and processing times.
*
* @see BaseImportStrategy
* @See FileType
* @See PromptResponsePair
*/

@Component
@Slf4j
public class JSONImportStrategy extends BaseImportStrategy {
    private final ObjectMapper objectMapper;

    public JSONImportStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public FileType getSupportedFileType() {
        return FileType.JSON;
    }

    @Override
    public boolean validateFile(MultipartFile file) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            validateFileSize(file);
            
            // Read the JSON content
            JsonNode rootNode = objectMapper.readTree(file.getInputStream());
            
            // Validate that it's an array
            if (!rootNode.isArray()) {
                throw new FileValidationException("JSON file must contain an array of prompt-response pairs");
            }

            // Validate array size
            if (rootNode.size() < MIN_ROWS || rootNode.size() > MAX_ROWS) {
                throw new FileValidationException(
                    String.format("Number of entries must be between %d and %d", MIN_ROWS, MAX_ROWS)
                );
            }

            // Validate structure of each entry
            for (JsonNode entry : rootNode) {
                if (!entry.has("prompt") || !entry.has("response")) {
                    throw new FileValidationException("Each entry must contain 'prompt' and 'response' fields");
                }
            }

            return true;
        } catch (IOException e) {
            logProcessingError("JSON validation failed", e);
            return false;
        } finally {
            sample.stop(meterRegistry.timer("import.validation.time", "type", "json"));
        }
    }

    @Override
    public List<PromptResponsePair> processImport(MultipartFile file, DataImport dataImport) {
        Timer.Sample sample = Timer.start(meterRegistry);
        List<PromptResponsePair> pairs = new ArrayList<>();
        AtomicInteger processedRows = new AtomicInteger(0);
        AtomicInteger errorRows = new AtomicInteger(0);

        try {
            JsonNode rootNode = objectMapper.readTree(file.getInputStream());
            
            for (JsonNode entry : rootNode) {
                try {
                    PromptResponsePair pair = new PromptResponsePair();
                    pair.setDataImport(dataImport);
                    pair.setPrompt(entry.get("prompt").asText());
                    pair.setResponse(entry.get("response").asText());
                    pair.setOriginalRowNumber(processedRows.incrementAndGet());
                    pair.setProcessingStatus(ProcessingStatus.PENDING);
                    
                    // Additional metadata if available
                    if (entry.has("metadata")) {
                        pair.setMetadata(entry.get("metadata").toString());
                    }

                    validatePair(pair);
                    pairs.add(pair);
                } catch (Exception e) {
                    errorRows.incrementAndGet();
                    logProcessingError("Error processing JSON entry " + processedRows.get(), e);
                }
            }

            recordMetrics("import.processed.rows", processedRows.get());
            recordMetrics("import.error.rows", errorRows.get());

            return pairs;
        } catch (Exception e) {
            logProcessingError("JSON processing failed", e);
            throw new FileProcessingException("Failed to process JSON file: " + e.getMessage());
        } finally {
            sample.stop(meterRegistry.timer("import.processing.time", "type", "json"));
        }
    }
}
