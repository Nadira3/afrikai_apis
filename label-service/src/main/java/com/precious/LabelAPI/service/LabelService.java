package com.precious.LabelAPI.service;

import com.precious.LabelAPI.dto.LabelingSubmissionDto;
import com.precious.LabelAPI.model.DataLabelingSubmission;
import com.precious.LabelAPI.repository.DataLabelingSubmissionRepository;
import com.precious.LabelAPI.exceptions.FailedSubmissionException;
import com.precious.LabelAPI.exceptions.FailedReviewException;
import com.precious.LabelAPI.model.enums.ProcessingStatus;
import com.precious.LabelAPI.dto.ReviewRequest;
import com.precious.LabelAPI.repository.PromptResponsePairRepository;
import com.precious.LabelAPI.model.PromptResponsePair;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class LabelService {

	private final DataLabelingSubmissionRepository dataLabelingSubmissionRepository;
	private final PromptResponsePairRepository promptResponsePairRepository;

	public LabelService(DataLabelingSubmissionRepository dataLabelingSubmissionRepository,
			PromptResponsePairRepository promptResponsePairRepository) {
		this.promptResponsePairRepository = promptResponsePairRepository;
		this.dataLabelingSubmissionRepository = dataLabelingSubmissionRepository;
	}

	public DataLabelingSubmission submitLabel(LabelingSubmissionDto submission, UUID taskId) {
		PromptResponsePair pair = promptResponsePairRepository.findById(submission.pairId())
				.orElseThrow(() -> new FailedSubmissionException("PromptResponsePair not found"));
		try {
			DataLabelingSubmission entity = submission.toEntity();
			entity.setTaskId(taskId);
			pair.setProcessingStatus(ProcessingStatus.LABELED);
			entity.setPromptResponsePair(pair);

			// Save entity to database
			DataLabelingSubmission savedSubmission = dataLabelingSubmissionRepository.save(entity);
			return savedSubmission;
		} catch (Exception e) {
			pair.setProcessingStatus(ProcessingStatus.ERROR);
			throw new FailedSubmissionException("Failed to submit label");
		}
	}

	public DataLabelingSubmission reviewLabel(ReviewRequest review, UUID subId) {
		try {
			DataLabelingSubmission submission = dataLabelingSubmissionRepository.findById(subId)
					.orElseThrow(() -> new FailedReviewException("Submission not found"));
			submission.setReview(review.review());


			// Save entity to database
			DataLabelingSubmission response = dataLabelingSubmissionRepository.save(submission);

			// set pair processing status to reviewed
			PromptResponsePair pair = response.getPromptResponsePair();
			pair.setProcessingStatus(ProcessingStatus.REVIEWED);
			promptResponsePairRepository.save(pair);
			return response;

		} catch (Exception e) {
			throw new FailedReviewException("Failed to review label: " + e.getMessage());
		}
	}
}
