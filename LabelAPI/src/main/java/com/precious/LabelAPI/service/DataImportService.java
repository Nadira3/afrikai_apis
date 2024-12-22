package com.precious.LabelAPI.service;

import com.precious.LabelAPI.dto.DataImportRequestDto;
import com.precious.LabelAPI.dto.DataImportResponseDto;
import com.precious.LabelAPI.model.enums.FileType;
import com.precious.LabelAPI.model.enums.ImportStatus;
import com.precious.LabelAPI.exceptions.FileProcessingException;
import com.precious.LabelAPI.exceptions.UnsupportedFileTypeException;
import com.precious.LabelAPI.model.DataImport;
import com.precious.LabelAPI.model.PromptResponsePair;
import com.precious.LabelAPI.repository.DataImportRepository;
import com.precious.LabelAPI.repository.PromptResponsePairRepository;
import com.precious.LabelAPI.service.strategy.DataImportStrategy;
import com.precious.LabelAPI.util.FileManagerUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import reactor.core.publisher.Mono;


/**
 * Data Import Service
 * Service to handle data import operations
 * Uses strategies to process different file types
 * Saves import metadata and prompt-response pairs
 * Handles file validation and error handling
 * Uses external entity service to validate client
 * Uses reactive programming with Project Reactor
 */
@Service
@Transactional
@Slf4j
public class DataImportService {
    
    private final Map<FileType, DataImportStrategy> importStrategies;
    private final ExternalEntityService externalEntityService;
    private final DataImportRepository dataImportRepository;
    private final PromptResponsePairRepository promptResponsePairRepository;

    public DataImportService(
        List<DataImportStrategy> strategies,
        ExternalEntityService externalEntityService,
        DataImportRepository dataImportRepository,
        PromptResponsePairRepository promptResponsePairRepository
    ) {
        /**
	 * The constructor initializes the service with a list of import strategies, external entity service, and repositories.
	 *
	 * The import strategies are mapped to their supported file types for easy access.
	 * The external entity service is used to validate the client.
	 * The data import repository is used to save import metadata.
	 * The prompt-response pair repository is used to save the results of the import.
	 *
	 * The service uses reactive programming with Project Reactor to handle asynchronous processing.
	 */
        this.importStrategies = strategies.stream() // Convert list of strategies to map
	    /**
	     * The import strategies are converted to a map using the supported file type as the key.
	     * This allows for easy access to the strategy based on the file type.
	     * The map is created using the Collectors.toMap method, which takes a key mapper and value mapper.
	     * The key mapper extracts the supported file type from the strategy.
	     * The value mapper returns the strategy itself.
	     *
	     * For example, if a strategy supports CSV files, the key will be FileType.CSV and the value will be the strategy.
	     * This allows the service to determine the strategy based on the file type when processing an import.
	     */
            .collect(Collectors.toMap(
                DataImportStrategy::getSupportedFileType, // Key mapper extracts supported file type
                Function.identity() // Identity function returns the element itself
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
     * 6. Returns import response
     */
    // The method processes a file upload request for a specific client and returns the result wrapped in Mono.
    public Mono<?> importData(DataImportRequestDto requestDto) {
        MultipartFile file = requestDto.getFile();
        UUID clientId = requestDto.getClientId();

        return externalEntityService.getClientReference(clientId)
            .flatMap(client -> {
                try {
                    // Determine file type and get appropriate strategy
                    FileType fileType = FileManagerUtil.determineFileType(file.getOriginalFilename());
                    DataImportStrategy strategy = importStrategies.get(fileType);

		    // If the file type is not supported, an UnsupportedFileTypeException is thrown.
                    if (strategy == null) {
                        return Mono.error(new UnsupportedFileTypeException(fileType.name()));
                    }

                    // Create import record
                    DataImport dataImport = new DataImport(file.getOriginalFilename(), fileType);
                    dataImport.setClientId(clientId);
                    dataImport.setImportStatus(ImportStatus.PROCESSING);

                    // Save initial import record
                    dataImport = dataImportRepository.save(dataImport);

                    // Process file reactively
                    return processFile(file, strategy, dataImport);
                    
                } catch (Exception e) {
                    return Mono.error(new FileProcessingException(e.getMessage()));
                }
            });
    }

    /**
     * Processes the file reactively
     * 1. Validates file format
     * 2. Processes file using strategy
     * 3. Saves the result and updates status
     */
    private Mono<DataImportResponseDto> processFile(MultipartFile file, DataImportStrategy strategy, DataImport dataImport) {
	    /**
	     * fromCallable method creates a Mono from a Callable, which is a functional interface that takes no arguments and returns a result.
	     *
	     * Callable method is used to process the file asynchronously and return the result as a Mono.
	     * Callable is a functional interface that takes no arguments and returns a result.
	     */
        return Mono.fromCallable(() -> {
            // Validate file format
            if (!strategy.validateFile(file)) {
                updateImportStatus(dataImport, ImportStatus.FAILED);
                return DataImportResponseDto.fromEntity(dataImport);
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

            return DataImportResponseDto.fromEntity(dataImport);
        }).doOnError(e -> {
            log.error("Error processing import {}: {}", dataImport.getId(), e.getMessage());
            updateImportStatus(dataImport, ImportStatus.FAILED);
        });
    }

    /**
     * Updates the status of an import
     */
    private void updateImportStatus(DataImport dataImport, ImportStatus status) {
        dataImport.setImportStatus(status);
        dataImportRepository.save(dataImport);
    }
}
