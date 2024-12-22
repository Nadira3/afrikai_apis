package com.precious.LabelAPI.dto;

import com.precious.LabelAPI.model.enums.FileType;

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

    @NotBlank(message = "File name is required")
    private String fileName;

    @NotNull(message = "File type is required")
    private FileType fileType;

    @NotNull(message = "File is required")
    private MultipartFile file;

    @NotBlank(message = "Client ID is required")
    private UUID clientId;
}
