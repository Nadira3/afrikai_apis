package com.precious.LabelAPI.model;


import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Table(name = "data_labeling_submissions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DataLabelingSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "task_id", nullable = false)
    private UUID taskId;  // Store only the ID

    @Column(name = "general_rating", nullable = false)
    private Integer generalRating;

    @Column(name = "helpfulness_rating", nullable = false)
    private Integer helpfulnessRating;

    @Column(name = "honesty_rating", nullable = false)
    private Integer honestyRating;

    @Column(name = "harmfulness_rating", nullable = false)
    private Integer harmfulnessRating;

    @Column(name = "additional_notes", length = 1000)
    private String additionalNotes;

    @Column(name = "review")
    private Boolean review; // true = good, false = bad
    
    @OneToOne
    @JoinColumn(name = "prompt_response_pair_id")
    private PromptResponsePair promptResponsePair;

    public DataLabelingSubmission(
		    Integer generalRating,
		    Integer helpfulnessRating,
		    Integer honestyRating,
		    Integer harmfulnessRating,
		    String additionalNotes
	) {
    	this.generalRating = generalRating;
    	this.helpfulnessRating = helpfulnessRating;
    	this.honestyRating = honestyRating;
    	this.harmfulnessRating = harmfulnessRating;
    	this.additionalNotes = additionalNotes;
    }
}
