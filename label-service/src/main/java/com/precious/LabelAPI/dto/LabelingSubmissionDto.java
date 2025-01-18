package com.precious.LabelAPI.dto;

import com.precious.LabelAPI.model.DataLabelingSubmission;
import java.util.UUID;

/**
 * DTO for submitting labeling data
 */
public record LabelingSubmissionDto(
    UUID pairId, // PromptResponsePair ID
    Integer generalRating,
    Integer helpfulnessRating,
    Integer honestyRating,
    Integer harmfulnessRating,
    String additionalNotes
) {
    public LabelingSubmissionDto {
        // Validation logic
        if (generalRating < 1 || generalRating > 5) {
		throw new IllegalArgumentException("General rating must be between 1 and 5");
        }
        if (helpfulnessRating < 1 || helpfulnessRating > 5) {
		throw new IllegalArgumentException("Helpfulness rating must be between 1 and 5");
        }
        if (honestyRating < 1 || honestyRating > 5) {
        	throw new IllegalArgumentException("Honesty rating must be between 1 and 5");
        }
	if (harmfulnessRating < 1 || harmfulnessRating > 5) {
		throw new IllegalArgumentException("Harmfulness rating must be between 1 and 5");
	}
	if (additionalNotes.isEmpty() || additionalNotes.length() < 10) {
		throw new IllegalArgumentException("Additional notes must be at least ten characters long");
	}
	    
    }

    // Helper method to convert DTO to entity
    public DataLabelingSubmission toEntity() {
        DataLabelingSubmission entity = new DataLabelingSubmission();
        entity.setGeneralRating(this.generalRating);
        entity.setHelpfulnessRating(this.helpfulnessRating);
        entity.setHonestyRating(this.honestyRating);
        entity.setHarmfulnessRating(this.harmfulnessRating);
        entity.setAdditionalNotes(this.additionalNotes);
        return entity;
    }
}

