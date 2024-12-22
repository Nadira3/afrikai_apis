package com.precious.LabelAPI.service.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.precious.LabelAPI.model.DataImport;
import com.precious.LabelAPI.model.PromptResponsePair;
import com.precious.LabelAPI.model.enums.FileType;
import com.precious.LabelAPI.model.enums.ProcessingStatus;

import io.micrometer.core.instrument.Timer;
import jakarta.validation.ValidationException;

import com.precious.LabelAPI.exceptions.FileProcessingException;
import com.precious.LabelAPI.exceptions.FileValidationException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
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

@Component // marks the class as a Spring Bean so it can be injected into other components
@Slf4j // Lombok annotation to generate a logger field
public class JSONImportStrategy extends BaseImportStrategy {
    private final ObjectMapper objectMapper;

    public JSONImportStrategy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    // Returns the supported file type
    public FileType getSupportedFileType() {
        return FileType.JSON;
    }

    @Override
    /**
     * Validate the JSON file
     * - Check file size
     *   - If the file size is too large, throw an exception
     *   - If the file size is within the limits, continue with the validation
     *
     * - Read the JSON content
     *   - If the content is not an array, throw an exception
     *   - If the content is an array, continue with the validation
     *
     * - Validate the array size
     *   - If the array size is not within the limits, throw an exception
     *   - If the array size is within the limits, continue with the validation
     *
     * - Validate the structure of each entry
     *   - If an entry does not contain 'prompt' and 'response' fields, throw an exception
     *   - If all entries are valid, return true
     *   - If any validation fails, return false
     */
    public boolean validateFile(MultipartFile file) {
        Timer.Sample sample = Timer.start(meterRegistry); // Start a timer to record the validation time
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
		if (entry.has("metadata") && !entry.get("metadata").isObject()) {
		    throw new FileValidationException("Metadata must be a valid JSON object");
		}
            }

            return true;
        } catch (IOException e) {
            logProcessingError("JSON validation failed due to IO error", e);
            return false;
	} catch (FileValidationException e) {
	    logProcessingError("File validation failed", e);
	    return false;
        } finally {
            sample.stop(meterRegistry.timer("import.validation.time", "type", "json"));
        }
    }

    @Override
    /**
     * Process the JSON file
     * - Read the JSON content
     * - For each entry in the JSON array
     *   - Create a new PromptResponsePair object
     *   - Set the prompt, response, and metadata fields
     *   - Validate the pair
     *   - Add the pair to the list of pairs
     * - Record metrics for processed and error rows
     * - Return the list of pairs
     */
    public List<PromptResponsePair> processImport(MultipartFile file, DataImport dataImport) {
        Timer.Sample sample = Timer.start(meterRegistry); // Start a timer to record the processing time
        List<PromptResponsePair> pairs = new ArrayList<>();
        AtomicInteger processedRows = new AtomicInteger(0);
        AtomicInteger errorRows = new AtomicInteger(0);

	/**
	 * @See CSVImportStrategy.java for a detailed explanation of the processImport method
	 * The JSON import strategy follows a similar process, but with JSON-specific logic
	 * The JSON content is read as a tree structure, and each entry is processed as a JSON object
	 * The prompt and response fields are extracted from each entry and used to create a PromptResponsePair object
	 * Additional metadata is extracted if available
	 * The pair is validated and added to the list of pairs
	 * Metrics are recorded for processed and error rows
	 * The list of pairs is returned
	 *
	 * @See BaseImportStrategy.java for the validatePair method
	 * @See PromptResponsePair.java for the PromptResponsePair model
	 * @See ProcessingStatus.java for the ProcessingStatus enum
	 * @See FileProcessingException.java for the FileProcessingException class
	 */
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

    /**
     * Validates the prompt and response fields of a PromptResponsePair.
     * Throws ValidationException if the pair is invalid.
     * 
     * @param pair the PromptResponsePair to validate
     */
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
