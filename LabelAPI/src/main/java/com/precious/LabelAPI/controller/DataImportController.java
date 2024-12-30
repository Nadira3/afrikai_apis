package com.precious.LabelAPI.controller;

import com.precious.LabelAPI.service.DataImportService;
import com.precious.LabelAPI.dto.DataImportRequestDto;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * DataImportController
 * This class is a controller class that handles the import of data from any file
 * type(Csv, json, xls, xlsx) to the database.
 */
@RestController
@RequestMapping("/api/label")
public class DataImportController {

    private final DataImportService dataImportService;

    @Autowired
    public DataImportController(DataImportService dataImportService) {
        this.dataImportService = dataImportService;
    }

    @PostMapping("/upload")
    public Mono<ResponseEntity<DataImportResponseDto>> importData(@Valid @ModelAttribute DataImportRequestDto dataImportRequestDto) {
        return dataImportService.importData(dataImportRequestDto)
            .map(response -> ResponseEntity.ok(response)) // map the result into ResponseEntity
            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new DataImportResponseDto(UUID.randomUUID(), "Error", 0, ImportStatus.FAILED, LocalDateTime.now())))); // error handling
    }
}
