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
@RequestMapping("/api")
public class DataImportController {

    // Autowire the DataImportService
    @Autowired
    private DataImportService dataImportService;

    @PostMapping("/upload")
    public ResponseEntity<?> importData(@Valid @ModelAttribute DataImportRequestDto dataImportRequestDto) {
        try {
            dataImportService.importData(dataImportRequestDto);
            return ResponseEntity.ok().body("Data imported successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error importing data: " + e.getMessage());
        }
    }
}
