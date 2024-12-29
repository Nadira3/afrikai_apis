package com.precious.TaskApi.model.content;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;

import java.util.HashSet;
import java.util.List;

@Entity
@Getter
@Setter
public class TrainingContent extends TaskContent {
    private HashSet<String> participants;

    public TrainingContent(Long id, List<String> questions, List<String> answers, HashSet<String> participants) {
        super(questions, answers);
        this.participants = participants;
    }
}
