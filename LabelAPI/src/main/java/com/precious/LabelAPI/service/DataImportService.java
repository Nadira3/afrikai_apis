package com.precious.LabelAPI.service;

import com.precious.LabelAPI.dto.DataImportResponseDto;
import com.precious.LabelAPI.enums.FileType;
import com.precious.LabelAPI.enums.ImportStatus;
import com.precious.LabelAPI.exceptions.FileProcessingException;
import com.precious.LabelAPI.exceptions.UnsupportedFileTypeException;
import com.precious.LabelAPI.model.DataImport;
import com.precious.LabelAPI.model.PromptResponsePair;
import com.precious.LabelAPI.repository.DataImportRepository;
import com.precious.LabelAPI.repository.PromptResponsePairRepository;
import com.precious.LabelAPI.strategy.DataImportStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;


// Data Import Service
// Service to handle data import operations
// Uses strategies to process different file types
// Saves import metadata and prompt-response pairs
// Handles file validation and error handling
// Uses external entity service to validate client
// Uses reactive programming with Project Reactor
@Service
@Transactional
@Slf4j
public class DataImportService {
    private final Map<FileType, DataImportStrategy> importStrategies;
    private final ExternalEntityService externalEntityService;
    private final DataImportRepository dataImportRepository;
    private final PromptResponsePairRepository promptResponsePairRepository;

    @Autowired
    public DataImportService(
        List<DataImportStrategy> strategies,
        ExternalEntityService externalEntityService,
        DataImportRepository dataImportRepository,
        PromptResponsePairRepository promptResponsePairRepository
    ) {
        // Initialize strategies map using stream
        this.importStrategies = strategies.stream()
            .collect(Collectors.toMap(
                DataImportStrategy::getSupportedFileType,
                Function.identity()
            ));
        this.externalEntityService = externalEntityService;
        this.dataImportRepository = dataImportRepository;
        this.promptResponsePairRepository = promptResponsePairRepository;
    }

    /**
     * Processes a data import file
     * 1. Validates client exists
     * 2. Determines file type
     * 3. Validates file format
     * 4. Processes file using appropriate strategy
     * 5. Saves import metadata and prompt-response pairs
     */
    public Mono<DataImportResponseDto> importData(MultipartFile file, UUID clientId) {
        return externalEntityService.getClientReference(clientId)
            .flatMap(client -> {
                try {
                    // Determine file type and get appropriate strategy
                    FileType fileType = determineFileType(file.getOriginalFilename());
                    DataImportStrategy strategy = importStrategies.get(fileType);
                    
                    if (strategy == null) {
                        return Mono.error(new UnsupportedFileTypeException(fileType));
                    }

                    // Create import record
                    DataImport dataImport = new DataImport();
                    dataImport.setClientId(clientId);
                    dataImport.setFileName(file.getOriginalFilename());
                    dataImport.setFileType(fileType);
                    dataImport.setImportStatus(ImportStatus.PROCESSING);
                    dataImport.setImportedAt(LocalDateTime.now());
                    
                    // Save initial import record
                    dataImport = dataImportRepository.save(dataImport);
                    
                    // Process file asynchronously
                    CompletableFuture.runAsync(() -> {
                        try {
                            // Validate file
                            if (!strategy.validateFile(file)) {
                                updateImportStatus(dataImport, ImportStatus.FAILED, "Invalid file format");
                                return;
                            }

                            // Process file and create prompt-response pairs
                            List<PromptResponsePair> pairs = strategy.processImport(file, dataImport);
                            
                            // Update import with results
                            dataImport.setTotalRecords(pairs.size());
                            dataImport.setProcessedRecords(pairs.size());
                            dataImport.setImportStatus(ImportStatus.COMPLETED);
                            
                            // Save all pairs and update import
                            promptResponsePairRepository.saveAll(pairs);
                            dataImportRepository.save(dataImport);
                            
                        } catch (Exception e) {
                            log.error("Error processing import {}: {}", dataImport.getId(), e.getMessage());
                            updateImportStatus(dataImport, ImportStatus.FAILED, e.getMessage());
                        }
                    });

                    // Return initial response
                    return Mono.just(DataImportResponseDto.fromEntity(dataImport));
                    
                } catch (Exception e) {
                    return Mono.error(new FileProcessingException(e.getMessage()));
                }
            });
    }

    /**
     * Updates the status of an import
     */
    private void updateImportStatus(DataImport dataImport, ImportStatus status, String errorMessage) {
        dataImport.setImportStatus(status);
        dataImport.setErrorMessage(errorMessage);
        dataImportRepository.save(dataImport);
    }

    /**
     * Determines file type from filename
     */
    private FileType determineFileType(String filename) {
        String extension = FilenameUtils.getExtension(filename).toLowerCase();
        return switch (extension) {
            case "csv" -> FileType.CSV;
            case "json" -> FileType.JSON;
            case "jsonl" -> FileType.JSONL;
            case "xlsx", "xls" -> FileType.EXCEL;
            default -> throw new UnsupportedFileTypeException("Unsupported file type: " + extension);
        };
    }
}
