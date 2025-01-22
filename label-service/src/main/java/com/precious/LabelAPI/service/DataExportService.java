package com.precious.LabelAPI.service;

import java.util.stream.Collectors;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.precious.LabelAPI.model.PromptResponsePair;
import com.precious.LabelAPI.dto.PromptResponse;
import com.precious.LabelAPI.model.enums.ProcessingStatus;
import com.precious.LabelAPI.repository.PromptResponsePairRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataExportService {
    @Autowired
    private PromptResponsePairRepository promptResponsePairRepository;

    public Page<PromptResponse> getAllPromptResponsePairs(Pageable pageable) {
	/**
	  * Get all prompt response pairs from the database by page
	  * @param pageable
	  *
	  * map each prompt response pair to a prompt response object
	  * @return Page<PromptResponse>
	  */
	return promptResponsePairRepository.findAll(pageable)
		    .map(pair -> PromptResponse.fromPromptResponsePair(pair));
    }

    public PromptResponse getPromptResponsePairById(UUID id) {
        PromptResponsePair response = promptResponsePairRepository.findById(id).orElse(null);

	if (response != null) {
		return PromptResponse.fromPromptResponsePair(response);
	} else {
		return null;
	}
    }

    public List<PromptResponse> getPromptResponsePairsByDataImportId(UUID dataImportId) {
	    /**
	     * Get all prompt response pairs with the same data import id from the database
	     * @param dataImportId
	     *
	     * map each prompt response pair to a prompt response object
	     * form a list of prompt response objects
	     * @return List<PromptResponse>
	     */
	return promptResponsePairRepository.findByDataImportId(dataImportId)
		    .stream()
		    .map(pair -> PromptResponse.fromPromptResponsePair(pair))
		    .collect(Collectors.toList());
    }

    public List<PromptResponse> getPromptResponsePairsByProcessingStatus(ProcessingStatus status) {
	/**
	 * Get all prompt response pairs with the same processing status from the database
	 * @param status
	 *
	 * map each prompt response pair to a prompt response object
	 * form a list of prompt response objects
	 * @return List<PromptResponse>
	 */
        return promptResponsePairRepository.findByProcessingStatus(status)
		    .stream()
        	    .map(pair -> PromptResponse.fromPromptResponsePair(pair))
		    .collect(Collectors.toList());
    }
}
