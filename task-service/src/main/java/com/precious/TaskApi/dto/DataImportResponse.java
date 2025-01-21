package com.precious.TaskApi.dto;

import com.precious.TaskApi.model.enums.ImportStatus;

import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(description = "Data Import Response Data Transfer Object")
public class DataImportResponse {
	@Schema(description = "Import ID; Unique identifier for the import")
	private UUID importId;

	@Schema(description = "File name; Name of the file that was imported")
	private String fileName;

	@Schema(description = "Total records; Total number of records(rows/columns) successfully imported")
	private Integer totalRecords;

	@Schema(description = "Import status; Status of the import operation")
	private ImportStatus status;

	@Schema(description = "Imported at; Date and time the import was completed")
	private LocalDateTime importedAt;


    /**
     * Create a new DataImportResponse object with the given parameters
     * to represent an unsuccessful import.
     */
    public static DataImportResponse toErrorTemplate(String message) {
	    return new DataImportResponse(
			    null,
			    message,
			    0,
			    ImportStatus.FAILED,
			    LocalDateTime.now()
			);
    }

}
