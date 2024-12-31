package com.precious.TaskApi.model.task;

import java.time.Duration;
import java.util.Map;

import com.precious.TaskApi.model.content.ExamContent;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("EXAM")
public class Exam {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "exam_id")
	private UUID id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Duration duration;

    @Column(name = "passing_grade")
    private Double passingGrade;

    @ElementCollection
    private Map<Long, Double> participantScores;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "exam_content_id")  // The foreign key linking to the ExamContent
    private TaskContent examContent;  // New field to link content
	
    @OneToOne
    private Task task
}
