package com.precious.TaskApi.model.task;

import java.util.List;

import com.precious.TaskApi.model.content.WorkContent;
import com.precious.TaskApi.model.enums.Skill;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("WORK")
public class WorkTask extends Task {
    @OneToOne(cascade = CascadeType.ALL)
    private WorkContent content;

    private Integer complexity;

    @ElementCollection(targetClass = Skill.class)
    @CollectionTable(name = "work_task_skills", joinColumns = @JoinColumn(name = "task_id"))
    @Enumerated(EnumType.STRING) // Store enums as strings in the database
    private List<Skill> requiredSkills;
}