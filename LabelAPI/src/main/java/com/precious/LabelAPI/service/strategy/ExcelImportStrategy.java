package com.precious.LabelAPI.service.strategy;

import static com.precious.LabelAPI.service.strategy.BaseImportStrategy.*;

import io.micrometer.core.instrument.Timer;
import jakarta.validation.ValidationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.precious.LabelAPI.exceptions.FileProcessingException;
import com.precious.LabelAPI.exceptions.FileValidationException;
import com.precious.LabelAPI.model.DataImport;
import com.precious.LabelAPI.model.PromptResponsePair;
import com.precious.LabelAPI.model.enums.FileType;
import com.precious.LabelAPI.model.enums.ProcessingStatus;

import lombok.extern.slf4j.Slf4j;

/**
* Excel Import Strategy Implementation
* This class is responsible for handling Excel file imports.
* It validates the file and processes the data rows.
* It also records metrics for validation and processing times.
*
* @see BaseImportStrategy
* @See FileType
* @See PromptResponsePair
*
* @See CSVImportStrategy and @See JSONImportStrategy for similar implementations
* and annotations overview.
*/

@Component
@Slf4j
public class ExcelImportStrategy extends BaseImportStrategy {
    private static final String[] REQUIRED_HEADERS = {"prompt", "response"};

    @Override
    public FileType getSupportedFileType() {
        return FileType.EXCEL;
    }

    @Override
    public boolean validateFile(MultipartFile file) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            validateFileSize(file);
            
	    /**
	     * WorkbookFactory.create() is a method from Apache POI library
	     * that creates a new instance of a Workbook from an InputStream.
	     *
	     * The Workbook object represents the entire Excel document.
	     * It contains one or more sheets, which in turn contain rows and cells.
	     *
	     * The Workbook object is closed automatically when the try-with-resources block ends.
	     * This ensures that the workbook is closed even if an exception occurs.
	     *
	     * The Sheet object represents a single sheet within the workbook.
	     * It contains rows and cells, and can be accessed by index or name.
	     *
	     * The Row object represents a single row within a sheet.
	     * It contains cells, which can be accessed by index or name.
	     *
	     * The Cell object represents a single cell within a row.
	     * It contains a value, which can be a string, number, boolean, or formula.
	     *
	     * The Workbook, Sheet, Row, and Cell objects are all interfaces.
	     *
	     * The WorkbookFactory.create() method throws an exception if the file is not a valid Excel file.
	     * The exception is caught and logged, and the method returns false.
	     */
            try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);
                Row headerRow = sheet.getRow(0);
                
                if (headerRow == null) {
                    throw new FileValidationException("Excel file is empty");
                }

                // Validate headers
                List<String> headers = new ArrayList<>();
                headerRow.forEach(cell -> headers.add(cell.getStringCellValue().toLowerCase().trim()));
                
                List<String> missingHeaders = Arrays.stream(REQUIRED_HEADERS)
                    .filter(required -> !headers.contains(required))
                    .collect(Collectors.toList());

                if (!missingHeaders.isEmpty()) {
                    throw new FileValidationException("Missing required headers: " + String.join(", ", missingHeaders));
                }

                // Validate row count
                int rowCount = sheet.getPhysicalNumberOfRows() - 1; // Subtract header row
                if (rowCount < MIN_ROWS || rowCount > MAX_ROWS) {
                    throw new FileValidationException(
                        String.format("Row count must be between %d and %d", MIN_ROWS, MAX_ROWS)
                    );
                }

                return true;
            }
        } catch (Exception e) {
            logProcessingError("Excel validation failed", e);
            return false;
        } finally {
            sample.stop(meterRegistry.timer("import.validation.time", "type", "excel"));
        }
    }

    @Override
    public List<PromptResponsePair> processImport(MultipartFile file, DataImport dataImport) {
        Timer.Sample sample = Timer.start(meterRegistry);
        List<PromptResponsePair> pairs = new ArrayList<>();
        AtomicInteger processedRows = new AtomicInteger(0);
        AtomicInteger errorRows = new AtomicInteger(0);

	try {
		validateFile(file);
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            
            // Create header map
            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null) {
                    headerMap.put(cell.getStringCellValue().toLowerCase().trim(), i);
                }
            }

            // Process data rows
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) continue;

                try {
                    PromptResponsePair pair = new PromptResponsePair();
                    pair.setDataImport(dataImport);
                    pair.setPrompt(getCellValue(row.getCell(headerMap.get("prompt"))));
                    pair.setResponse(getCellValue(row.getCell(headerMap.get("response"))));
                    pair.setOriginalRowNumber(processedRows.incrementAndGet());
                    pair.setProcessingStatus(ProcessingStatus.PENDING);

                    // Handle additional columns if present
                    if (headerMap.containsKey("metadata")) {
			    log.info("Metadata column found, including metadata in import");
                        pair.setMetadata(getCellValue(row.getCell(headerMap.get("metadata"))));
                    }

                    validatePair(pair);
                    pairs.add(pair);
                } catch (Exception e) {
                    errorRows.incrementAndGet();
                    logProcessingError("Error processing Excel row " + rowNum, e);
                }
            }

            recordMetrics("import.processed.rows", processedRows.get());
            recordMetrics("import.error.rows", errorRows.get());

	    log.info("Processed {} rows with {} errors", processedRows.get(), errorRows.get());
            return pairs;
        } catch (Exception e) {
            logProcessingError("Excel processing failed", e);
            throw new FileProcessingException("Failed to process Excel file: " + e.getMessage());
        } finally {
            sample.stop(meterRegistry.timer("import.processing.time", "type", "excel"));
        }
	} catch (FileValidationException e) {
		logProcessingError("Excel validation failed", e);
		throw new FileProcessingException("Failed to process Excel file: " + e.getMessage());
	} catch (Exception e) {
		logProcessingError("Excel processing failed", e);
		throw new FileProcessingException("Failed to process Excel file: " + e.getMessage());
	}
    }

    /**
     * Get cell value as a string
     * @param cell the cell to get the value from
     * @return the cell value as a string
     */
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> "";
        };
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
