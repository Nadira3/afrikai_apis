package com.precious.TaskApi.model;

import com.precious.TaskApi.model.enums.ImportStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryResponse {
    private UUID importId;          // Unique identifier for the import process
    private String fileName;        // Name of the uploaded file
    private Integer totalRecords;   // Number of records processed
    private ImportStatus status;    // Status of the import (e.g., SUCCESS, FAILED)
    private LocalDateTime importedAt; // Timestamp when the import was processed
}
