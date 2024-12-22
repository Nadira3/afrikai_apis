package com.precious.LabelAPI.repository;

import com.precious.LabelAPI.model.DataImport;
import com.precious.LabelAPI.model.PromptResponsePair;
import com.precious.LabelAPI.model.enums.ProcessingStatus;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Prompt Response Pair Repository
// Repository for prompt-response pairs
// Extends JpaRepository for CRUD operations
@Repository
public interface PromptResponsePairRepository extends JpaRepository<PromptResponsePair, UUID> {
	// Find all pairs from a specific Dataset
	List<PromptResponsePair> findByDataImport(DataImport dataImport);

	// Find all pairs from a specific Dataset by status
	List<PromptResponsePair> findByProcessingStatus(ProcessingStatus status);
}
