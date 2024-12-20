package com.precious.LabelAPI.service.strategy;

/**
* Excel Import Strategy Implementation
* This class is responsible for handling Excel file imports.
* It validates the file and processes the data rows.
* It also records metrics for validation and processing times.
*
* @see BaseImportStrategy
* @See FileType
* @See PromptResponsePair
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

            return pairs;
        } catch (Exception e) {
            logProcessingError("Excel processing failed", e);
            throw new FileProcessingException("Failed to process Excel file: " + e.getMessage());
        } finally {
            sample.stop(meterRegistry.timer("import.processing.time", "type", "excel"));
        }
    }

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
}
