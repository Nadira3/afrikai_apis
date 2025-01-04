package com.precious.LabelAPI.service.strategy;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.precious.LabelAPI.model.DataImport;
import com.precious.LabelAPI.model.PromptResponsePair;
import com.precious.LabelAPI.model.enums.FileType;

// Interface for Data Import Strategy
// This interface is used to define the methods that will be implemented by the classes that will be used to import data from different file types
public interface DataImportStrategy {
    FileType getSupportedFileType();
    boolean validateFile(MultipartFile file);
    List<PromptResponsePair> processImport(MultipartFile file, DataImport dataImport);
}

