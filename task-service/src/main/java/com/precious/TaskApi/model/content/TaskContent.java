package com.precious.TaskApi.model.content;

import java.util.List;
import java.util.UUID;


import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    
    @Column(name = "question", columnDefinition = "TEXT")
    private List<String> questions;
    
    @Column(name = "answer", columnDefinition = "TEXT")
    private List<String> answers;

    public TaskContent(List<String> questions, List<String> answers) {
        this.questions = questions;
        this.answers = answers;
    }
}
