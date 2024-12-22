package com.precious.LabelAPI.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.precious.LabelAPI.model.enums.ProcessingStatus;
import com.precious.LabelAPI.model.DataImport;

/**
 * Represents a single prompt-response pair for labeling
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "prompt_response_pairs")
public class PromptResponsePair {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "prompt", length = 4000, nullable = false)
    private String prompt;

    @Column(name = "response", length = 8000, nullable = false)
    private String response;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_import_id", nullable = false)
    private DataImport dataImport;

    @OneToOne(mappedBy = "promptResponsePair")
    private DataLabelingSubmission labelingSubmission;

    @Column(name = "original_row_number")
    private Integer originalRowNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status")
    private ProcessingStatus processingStatus;

// Constructor for creating a new prompt-response pair
    public PromptResponsePair(
	String prompt,
	String response,
	DataImport dataImport,
	Integer originalRowNumber
    ) {
	this.prompt = prompt;
	this.response = response;
	this.dataImport = dataImport;
	this.originalRowNumber = originalRowNumber;
	this.processingStatus = ProcessingStatus.PENDING;
    }

    public void setMetadata(String cellValue) {
        // method to set metadata of prompt-response pair if available
        
    }
}
