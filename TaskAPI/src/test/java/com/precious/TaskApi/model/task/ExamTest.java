package com.precious.TaskApi.model.task;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.precious.TaskApi.model.content.ExamContent;
import com.precious.TaskApi.model.enums.TaskCategory;
import com.precious.TaskApi.model.enums.TaskType;

class ExamTest {
    
    private Exam exam;
    private LocalDateTime now;
    
    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        exam = new Exam();
        
        // Set inherited Task properties
        exam.setId(1L);
        exam.setClientId("client123");
        exam.setTitle("Math Exam");
        exam.setDescription("Final Math Examination");
        exam.setReward(50.0);
        exam.setCreatedAt(now);
        exam.setCreatedBy(1L);
        exam.setCategory(TaskCategory.LABEL);
        exam.setType(TaskType.EXAM);
        
        // Set Exam-specific properties
        exam.setContent("Exam questions content");
        exam.setDuration(Duration.ofHours(2));
        exam.setPassingGrade(70.0);
        exam.setParticipantScores(new HashMap<>());
    }
    
    @Test
    void testInheritedProperties() {
        assertThat(exam.getId()).isEqualTo(1L);
        assertThat(exam.getClientId()).isEqualTo("client123");
        assertThat(exam.getTitle()).isEqualTo("Math Exam");
        assertThat(exam.getDescription()).isEqualTo("Final Math Examination");
        assertThat(exam.getReward()).isEqualTo(50.0);
        assertThat(exam.getCreatedAt()).isEqualTo(now);
        assertThat(exam.getCreatedBy()).isEqualTo(1L);
        assertThat(exam.getCategory()).isEqualTo(TaskCategory.LABEL);
        assertThat(exam.getType()).isEqualTo(TaskType.EXAM);
    }
    
    @Test
    void testExamSpecificProperties() {
        assertThat(exam.getContent()).isEqualTo("Exam questions content");
        assertThat(exam.getDuration()).isEqualTo(Duration.ofHours(2));
        assertThat(exam.getPassingGrade()).isEqualTo(70.0);
        assertThat(exam.getParticipantScores()).isEmpty();
    }
    
    @Test
    void testParticipantScores() {
        Map<Long, Double> scores = new HashMap<>();
        scores.put(1L, 85.5);
        scores.put(2L, 92.0);
        
        exam.setParticipantScores(scores);
        
        assertThat(exam.getParticipantScores())
            .hasSize(2)
            .containsEntry(1L, 85.5)
            .containsEntry(2L, 92.0);
    }
    
    @Test
    void testExamContent() {
        ExamContent examContent = new ExamContent();
        examContent.setId(1L);
        // Set other ExamContent properties as needed
        
        exam.setExamContent(examContent);
        
        assertThat(exam.getExamContent())
            .isNotNull()
            .extracting(ExamContent::getId)
            .isEqualTo(1L);
    }
    
    @Test
    void testAllArgsConstructor() {
        ExamContent examContent = new ExamContent();
        Map<Long, Double> scores = new HashMap<>();
        scores.put(1L, 95.0);
        
        Exam newExam = new Exam(
            "Test content",
            Duration.ofHours(3),
            80.0,
            scores,
            examContent
        );
        
        assertThat(newExam.getContent()).isEqualTo("Test content");
        assertThat(newExam.getDuration()).isEqualTo(Duration.ofHours(3));
        assertThat(newExam.getPassingGrade()).isEqualTo(80.0);
        assertThat(newExam.getParticipantScores())
            .hasSize(1)
            .containsEntry(1L, 95.0);
        assertThat(newExam.getExamContent()).isEqualTo(examContent);
    }
    
    @Test
    void testNoArgsConstructor() {
        Exam newExam = new Exam();
        
        assertThat(newExam.getContent()).isNull();
        assertThat(newExam.getDuration()).isNull();
        assertThat(newExam.getPassingGrade()).isNull();
        assertThat(newExam.getParticipantScores()).isNull();
        assertThat(newExam.getExamContent()).isNull();
    }
}