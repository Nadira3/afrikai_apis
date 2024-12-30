package com.precious.LabelAPI.repository;

import com.precious.LabelAPI.model.DataLabelingSubmission;
import com.precious.LabelAPI.model.PromptResponsePair;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataLabelingSubmissionRepository extends JpaRepository<DataLabelingSubmission, UUID> {

	//  Find all the data labeling submission by data labeling id
	Optional<DataLabelingSubmission> findById(UUID dataLabelingId);

	// Find all the data labeling submission by data labeling id and user id
	List<DataLabelingSubmission> findByIdAndTaskId(UUID dataLabelingId, UUID taskId);

	// Find submission by prompt id
	// This is used to check if a user has sent a submission for a prompt-response pair
	Optional<DataLabelingSubmission> findByPromptResponsePair(PromptResponsePair promptResponsePair);

}
