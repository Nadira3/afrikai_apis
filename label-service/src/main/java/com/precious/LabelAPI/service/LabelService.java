package com.precious.LabelAPI.service;

import com.precious.LabelAPI.dto.LabelingSubmissionDto;
import com.precious.LabelAPI.model.DataLabelingSubmission;
import com.precious.LabelAPI.repository.DataLabelingSubmissionRepository;
import com.precious.LabelAPI.exceptions.FailedSubmissionException;
import com.precious.LabelAPI.exceptions.FailedReviewException;
import com.precious.LabelAPI.model.enums.ProcessingStatus;
import com.precious.LabelAPI.dto.ReviewRequest;
import com.precious.LabelAPI.repository.PromptResponsePairRepository;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class LabelService {

    private final DataLabelingSubmissionRepository dataLabelingSubmissionRepository;
    private final PromptResponsePairRepository promptResponsePairRepository;

    public LabelService(DataLabelingSubmissionRepository dataLabelingSubmissionRepository, PromptResponsePairRepository promptResponsePairRepository) {
	this.promptResponsePairRepository = promptResponsePairRepository;
	this.dataLabelingSubmissionRepository = dataLabelingSubmissionRepository;
    }

    public DataLabelingSubmission submitLabel(LabelingSubmissionDto submission, UUID taskId) {
	    try {
	DataLabelingSubmission entity = submission.toEntity();
	entity.setTaskId(taskId);
	entity.setPromptResponsePair(promptResponsePairRepository.findById(submission.getPairId())
			.map(pair -> pair.setProcessingStatus(ProcessingStatus.LABELED))
			.orElseThrow(new FailedSubmissionException("PromptResponsePair not found")));



	// Save entity to database
		DataLabelingSubmission savedSubmission = dataLabelingSubmissionRepository.save(entity);
		return savedSubmission;
	} catch (Exception e) {
		pair.setProcessingStatus(ProcessingStatus.ERROR);
		throw new FailedSubmissionException("Failed to submit label");
	}
	return null;
    }

    public DataLabelingSubmission reviewLabel(ReviewRequest review, UUID subId) {
	    try {
		    DataLabelingSubmission submission = dataLabelingSubmissionRepository.findById(subId)
			    .map()
			    .orElseThrow(new FailedReviewException("Submission not found"));
		    submission.setReview(review.getReview());

		    // Save entity to database
		    DataLabelingSubmission response = dataLabelingSubmissionRepository.save(submission);

		    // set pair processing status to reviewed
		    submission.getPromptResponsePair().setProcessingStatus(ProcessingStatus.REVIEWED);
		    return response;

		} catch (Exception e) {
			throw new FailedReviewException("Failed to review label");
		}
		return null;
    }

}
