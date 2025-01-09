package com.precious.LabelAPI.service;

import com.precious.LabelAPI.dto.DataImportRequest;
import com.precious.LabelAPI.dto.DataImportResponse;
import com.precious.LabelAPI.model.enums.FileType;
import com.precious.LabelAPI.model.enums.ImportStatus;
import com.precious.LabelAPI.exceptions.FileProcessingException;
import com.precious.LabelAPI.exceptions.FileValidationException;
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
    private final DataImportRepository dataImportRepository;
    private final PromptResponsePairRepository promptResponsePairRepository;

    public DataImportService(
            List<DataImportStrategy> strategies,
            DataImportRepository dataImportRepository,
            PromptResponsePairRepository promptResponsePairRepository) {
        /**
         * The constructor initializes the service with a list of import strategies,
         * external entity service, and repositories.
         *
         * The import strategies are mapped to their supported file types for easy
         * access.
         * The external entity service is used to validate the client.
         * The data import repository is used to save import metadata.
         * The prompt-response pair repository is used to save the results of the
         * import.
         *
         * The service uses reactive programming with Project Reactor to handle
         * asynchronous processing.
         */
        this.importStrategies = strategies.stream() // Convert list of strategies to map
                /**
                 * The import strategies are converted to a map using the supported file type as
                 * the key.
                 * This allows for easy access to the strategy based on the file type.
                 * The map is created using the Collectors.toMap method, which takes a key
                 * mapper and value mapper.
                 * The key mapper extracts the supported file type from the strategy.
                 * The value mapper returns the strategy itself.
                 *
                 * For example, if a strategy supports CSV files, the key will be FileType.CSV
                 * and the value will be the strategy.
                 * This allows the service to determine the strategy based on the file type when
                 * processing an import.
                 */
                .collect(Collectors.toMap(
                        DataImportStrategy::getSupportedFileType, // Key mapper extracts supported file type
                        Function.identity() // Identity function returns the element itself
                ));
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
    // The method processes a file upload request for a specific client and returns
    // the result wrapped in Mono.
    public Mono<DataImportResponse> importData(DataImportRequest request) {
        MultipartFile file = request.getFile();
	log.info("Processing file: {}", file.getOriginalFilename());
        String clientId = request.getClientId();
        return Mono.fromCallable(() -> {
            try {
                // Determine file type and get appropriate strategy
                FileType fileType = FileManagerUtil.determineFileType(file.getOriginalFilename());
		log.info("File type: {}", fileType);
                DataImportStrategy strategy = importStrategies.get(fileType);

                // If the file type is not supported, throw error
                if (strategy == null) {
			log.error("Unsupported file type: {}", fileType.name());
			log.error("Supported file types: {}", importStrategies.keySet());
			log.info("Strategy is null");
                    throw new UnsupportedFileTypeException(fileType.name());
                }

		log.info("Strategy: {}", strategy.getSupportedFileType());
                // Create import record
                DataImport dataImport = new DataImport(file.getOriginalFilename(), fileType);
                dataImport.setClientId(clientId);
                dataImport.setImportStatus(ImportStatus.PROCESSING);

		log.info("Data import: {}", dataImport.toString());
                // Save the initial import record
                return dataImportRepository.save(dataImport);
            } catch (Exception e) {
                throw new FileProcessingException(e.getMessage());
            }
        }).flatMap(savedDataImport ->
            // Process file reactively and update status
            processFile(file, importStrategies.get(FileManagerUtil.determineFileType(file.getOriginalFilename())), savedDataImport)
                .map(fileProcessed -> DataImportResponse.fromEntity(savedDataImport)) // Map to response DTO
        );
    }

    /**
     * Processes the file reactively
     * 1. Validates file format
     * 2. Processes file using strategy
     * 3. Saves the result and updates status
     */
    private Mono<DataImportResponse> processFile(MultipartFile file, DataImportStrategy strategy,
            DataImport dataImport) {
        return Mono.fromCallable(() -> {
            // Validate file format
            try {
                if (!strategy.validateFile(file)) {
                    throw new FileValidationException("Invalid file format or size.");
                }

                // Process file and create prompt-response pairs
                List<PromptResponsePair> pairs = strategy.processImport(file, dataImport);

                // Update import with results
                dataImport.setTotalRecords(pairs.size());
                dataImport.setProcessedRecords(pairs.size());
                dataImport.setImportStatus(ImportStatus.SUCCESS);

                // Save all pairs and update import
                promptResponsePairRepository.saveAll(pairs);
                dataImportRepository.save(dataImport);

                return DataImportResponse.fromEntity(dataImport);

            } catch (FileValidationException e) {
                log.error("File validation failed for import {}: {}", dataImport.getId(), e.getMessage());
                updateImportStatus(dataImport, ImportStatus.FAILED);
                return DataImportResponse.fromEntity(dataImport);
            } catch (FileProcessingException e) {
                log.error("File processing failed for import {}: {}", dataImport.getId(), e.getMessage());
                updateImportStatus(dataImport, ImportStatus.FAILED);
                return DataImportResponse.fromEntity(dataImport);
            } catch (Exception e) {
                log.error("Unexpected error processing import {}: {}", dataImport.getId(), e.getMessage());
                updateImportStatus(dataImport, ImportStatus.FAILED);
                return DataImportResponse.fromEntity(dataImport);
            }
        }).doOnError(e -> {
            log.error("Unexpected error processing import {}: {}", dataImport.getId(), e.getMessage());
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
