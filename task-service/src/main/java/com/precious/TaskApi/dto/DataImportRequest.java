package com.precious.TaskApi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Import Request")
public class DataImportRequest {

    @NotNull(message = "File is required")
    @Schema(description = "File to import", required = true)
    private MultipartFile file;

    @NotBlank(message = "Client ID is required")
    @Schema(description = "Client ID", required = true)
    private String clientId;

    @NotBlank(message = "Task ID is required")
    @Schema(description = "Task ID", required = true)
    private UUID taskId;
}
