package com.precious.LabelAPI.controller;

import com.precious.LabelAPI.service.DataExportService;
import com.precious.LabelAPI.service.DataImportService;
import com.precious.LabelAPI.dto.DataImportRequestDto;
import com.precious.LabelAPI.dto.DataImportResponseDto;
import com.precious.LabelAPI.model.PromptResponsePair;
import com.precious.LabelAPI.model.enums.ImportStatus;
import com.precious.LabelAPI.model.enums.ProcessingStatus;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * DataImportController
 * This class is a controller class that handles the import of data from any
 * file
 * type(Csv, json, xls, xlsx) to the database.
 */
@RestController
@RequestMapping("/api/label")
public class DataImportController {

    private final DataImportService dataImportService;
    private final DataExportService dataExportService;

    public DataImportController(DataImportService dataImportService, DataExportService dataExportService) {
        this.dataImportService = dataImportService;
        this.dataExportService = dataExportService;
    }

    @PostMapping("/import")
    public ResponseEntity<DataImportResponseDto>> importData(
            @Valid @ModelAttribute DataImportRequestDto dataImportRequestDto) {

        // Ensure the importData method from service is returning
        // Mono<DataImportResponseDto>
        return dataImportService.importData(dataImportRequestDto)
		.map(dataImportResponseDto -> ResponseEntity.created(URI.create("/api/label/" + dataImportResponseDto.getId()))
			.body(dataImportResponseDto))
		.defaultIfEmpty(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/api/prompt-response-pairs")
    public Page<PromptResponsePair> getPromptResponsePairs(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size) {
        return dataExportService.getAllPromptResponsePairs(Pageable.ofSize(size).withPage(page));
    }

    // Ensure the endpoint is returning a single PromptResponsePair
    @GetMapping("/api/prompt-response-pairs/{id}")
    public ResponseEntity<PromptResponsePair> getPromptResponsePairById(@PathVariable UUID id) {
        // Ensure the service method is returning a Mono<PromptResponsePair>
        PromptResponsePair pair = dataExportService.getPromptResponsePairById(id);
        if (pair == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pair);
    }

    @GetMapping("/api/prompt-response-pairs/data-import/{id}")
    public ResponseEntity<List<PromptResponsePair>> getPromptResponsePairByDataImportId(@PathVariable UUID id) {
        List<PromptResponsePair> pairs = dataExportService.getPromptResponsePairsByDataImportId(id);
        if (pairs == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pairs);
    }

    @GetMapping("/api/prompt-response-pairs/processing-status/{status}")
    public ResponseEntity<List<PromptResponsePair>> getPromptResponsePairByProcessingStatus(@PathVariable String status) {
                List<PromptResponsePair> pairs = dataExportService.getPromptResponsePairsByProcessingStatus(ProcessingStatus.valueOf(status));
                if (pairs == null) {
                return ResponseEntity.notFound().build();
                }
                return ResponseEntity.ok(pairs);
        }
    
    


}
