package com.precious.LabelAPI.dto;

import com.precious.LabelAPI.model.DataImport;
import com.precious.LabelAPI.model.enums.ImportStatus;

import java.time.LocalDateTime;
import java.util.UUID;


/**
 * DTO for data import response
 * 
 * This class is used to represent the response to a data import request.
 * It contains the import ID, file name, total records, import status, and the time the import was completed.
`* @See DataImport
 */
public record DataImportResponse(
    UUID importId,
    String fileName,
    Integer totalRecords,
    ImportStatus status,
    LocalDateTime importedAt
) {
    // Static factory method to create from entity
    public static DataImportResponse fromEntity(DataImport entity) {
        return new DataImportResponse(
            entity.getId(),
            entity.getFileName(),
            entity.getTotalRecords(),
            entity.getImportStatus(),
            entity.getImportedAt()
        );
    }
}
