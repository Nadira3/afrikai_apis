package com.precious.TaskApi.model.content;

import jakarta.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamContent extends TaskContent {
    
    private Integer passingGrade;
}