package com.precious.TaskApi.dto;

import com.precious.TaskApi.model.enums.ImportStatus;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for data import response
 * 
 * This class is used to represent the response to a data import request.
 * It contains the import ID, file name, total records, import status, and the time the import was completed.
`* @See DataImport
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataImportResponse {
    private UUID importId;
    private String fileName;
    private Integer totalRecords;
    private ImportStatus status;
    private LocalDateTime importedAt;
}
