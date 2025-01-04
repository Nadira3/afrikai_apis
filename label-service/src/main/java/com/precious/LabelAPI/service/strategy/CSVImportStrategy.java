package com.precious.LabelAPI.service.strategy;

import com.precious.LabelAPI.model.DataImport;
import com.precious.LabelAPI.model.PromptResponsePair;
import com.precious.LabelAPI.model.enums.FileType;
import com.precious.LabelAPI.model.enums.ProcessingStatus;
import com.precious.LabelAPI.exceptions.FileProcessingException;
import com.precious.LabelAPI.exceptions.FileValidationException;


import com.opencsv.CSVReader;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.micrometer.core.instrument.Timer;
import jakarta.validation.ValidationException;


// CSV Implementation of the DataImportStrategy

@Component // Added this annotation to make the class a Spring bean
@Slf4j // Added this annotation to enable logging
public class CSVImportStrategy extends BaseImportStrategy {
    private static final String[] REQUIRED_HEADERS = {"prompt", "response"}; // This ensures the file format matches expectations
    
    /**
     * Indicates the supported file type for this strategy.
     * 
     * @return the supported file type
     */
    public FileType getSupportedFileType() {
        return FileType.CSV;
    }

    @Override
    /**
     * Validates the CSV file to ensure it meets the required criteria.
     * Throws FileValidationException if the file is invalid.
     * Logs success if validation passes.
     * 
     * @param file the file to validate
     * @return true if the file is valid, false otherwise
     */
    public boolean validateFile(MultipartFile file) {
        Timer.Sample sample = Timer.start(meterRegistry); // Tracks the time taken to validate a file
        try {
            validateFileSize(file);
           
	    // Use try-with-resources to automatically close the reader
            try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
		String[] headers = Arrays.stream(reader.readNext())
                         .map(String::trim)
                         .map(String::toLowerCase)
                         .toArray(String[]::new);

		// Check if the file is empty
                if (headers == null) {
                    throw new FileValidationException("CSV file is empty");
                }

                /**
		 * Check if the file contains the required headers
		 * Collects missing headers into a list
		 *
		 * @param required -> the required headers
		 * @param headers -> the headers in the file
		 * @return missingHeaders -> the missing headers
		 * @throws FileValidationException if the file is missing required headers
		 *
		 * @return true if the file is valid, false otherwise
		 *
		 * @see Arrays.stream
		 */
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

		/**
		 * Check if the row count is within the specified range
		 *
		 * @param rowCount -> the number of rows in the file
		 * @param MIN_ROWS -> the minimum number of rows allowed
		 * @param MAX_ROWS -> the maximum number of rows allowed
		 * @throws FileValidationException if the row count is outside the specified range
		 */
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
    /**
     * Processes the CSV file and returns a list of PromptResponsePair objects.
     * Throws FileProcessingException if the file processing fails.
     * 
     * @param file the file to process
     * @param dataImport the DataImport object to associate with the pairs
     * @return a list of PromptResponsePair objects
     */
    public List<PromptResponsePair> processImport(MultipartFile file, DataImport dataImport) {
        Timer.Sample sample = Timer.start(meterRegistry); // Tracks the time taken to process a file
        List<PromptResponsePair> pairs = new ArrayList<>();
	/**
	  * AtomicInteger is used to track the number of processed and errored rows
	  * by incrementing the value atomically
	  * This is necessary because the processing of rows is done in parallel
	  * and we need to ensure the counts are accurate
	  *
	  * we use the incrementAndGet() method to increment the value by 1
	  * and return the updated value
	  */
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

