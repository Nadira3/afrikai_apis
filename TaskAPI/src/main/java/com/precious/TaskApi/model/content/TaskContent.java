package com.precious.TaskApi.model.content;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class TaskContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ElementCollection
    @CollectionTable(name = "exam_questions", joinColumns = @JoinColumn(name = "exam_content_id"))
    @Column(name = "question", columnDefinition = "TEXT")
    private List<String> questions;
    
    @ElementCollection
    @CollectionTable(name = "exam_answers", joinColumns = @JoinColumn(name = "exam_content_id"))
    @Column(name = "answer", columnDefinition = "TEXT")
    private List<String> answers;

    public TaskContent(List<String> questions, List<String> answers) {
        this.questions = questions;
        this.answers = answers;
    }
}
