package com.precious.LabelAPI.dto;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.precious.LabelAPI.model.enums.ProcessingStatus;
import com.precious.LabelAPI.model.PromptResponsePair;

/**
 * Represents a single prompt-response pair for labeling
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromptResponse {
    private UUID responseId;

    private String prompt;

    private String response;

    private Integer originalRowNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status")
    private ProcessingStatus processingStatus;

    private String metadata;


    public static PromptResponse fromPromptResponsePair(PromptResponsePair pair) {
	    return new PromptResponse(pair.getId(), pair.getPrompt(), pair.getResponse(), pair.getOriginalRowNumber(), pair.getProcessingStatus(), pair.getMetadata());
    }
}
