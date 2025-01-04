package com.precious.LabelAPI.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.precious.LabelAPI.model.PromptResponsePair;
import com.precious.LabelAPI.model.enums.ProcessingStatus;
import com.precious.LabelAPI.repository.PromptResponsePairRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataExportService {
    @Autowired
    private PromptResponsePairRepository promptResponsePairRepository;

    public Page<PromptResponsePair> getAllPromptResponsePairs(Pageable pageable) {
        return promptResponsePairRepository.findAll(pageable);
    }

    public PromptResponsePair getPromptResponsePairById(UUID id) {
        return promptResponsePairRepository.findById(id).orElse(null);
    }

    public List<PromptResponsePair> getPromptResponsePairsByDataImportId(UUID dataImportId) {
        return promptResponsePairRepository.findByDataImportId(dataImportId);
    }

    public List<PromptResponsePair> getPromptResponsePairsByProcessingStatus(ProcessingStatus status) {
        return promptResponsePairRepository.findByProcessingStatus(status);
    }
}
