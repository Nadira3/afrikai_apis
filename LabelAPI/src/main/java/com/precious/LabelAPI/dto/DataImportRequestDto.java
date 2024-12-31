package com.precious.LabelAPI.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataImportRequestDto {

    @NotNull(message = "File is required")
    private MultipartFile file;

    @NotBlank(message = "Client ID is required")
    private UUID clientId;
}
