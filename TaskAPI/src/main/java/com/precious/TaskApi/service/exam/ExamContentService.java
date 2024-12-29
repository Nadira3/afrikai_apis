package com.precious.TaskApi.service.exam;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.precious.TaskApi.dto.task.TaskCreationDto;
import com.precious.TaskApi.exception.TaskCreationException;
import com.precious.TaskApi.model.content.ExamContent;
import com.precious.TaskApi.repository.ExamContentRepository;
import com.precious.TaskApi.service.web.TaskSpecificApiClient;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExamContentService {
    private final ExamContentRepository examContentRepository;
    private final TaskSpecificApiClient taskSpecificApiClient;
    
    @Data
    private static class QuestionAnswer {
        private String question;
        private String answer;
        
        public static QuestionAnswer fromString(String raw) {
            // Assuming the API returns format: "Q: What is X? | A: X is Y"
            String[] parts = raw.split("\\|");
            QuestionAnswer qa = new QuestionAnswer();
            qa.setQuestion(parts[0].substring(2).trim());
            qa.setAnswer(parts[1].substring(2).trim());
            return qa;
        }
    }

    public ExamContent createAndSaveExamContent(TaskCreationDto request) {
        ExamContent examContent = createExamContent(request);
        return saveExamContent(examContent);
    }
    
    public ExamContent createExamContent(TaskCreationDto request) {
        try {
            // Fetch combined question-answer pairs from API
            List<String> rawQAPairs = taskSpecificApiClient.fetchQuestionsAndAnswers(
                request.getCategory(), 
                request.getQuestionNumber()
            );
            
            // Parse and separate questions and answers
            List<QuestionAnswer> qaObjects = rawQAPairs.stream()
                .map(QuestionAnswer::fromString)
                .collect(Collectors.toList());
                
            // Extract questions and answers into separate lists
            List<String> questions = qaObjects.stream()
                .map(QuestionAnswer::getQuestion)
                .collect(Collectors.toList());
                
            List<String> answers = qaObjects.stream()
                .map(QuestionAnswer::getAnswer)
                .collect(Collectors.toList());
            
            // Calculate passing grade
            Integer passingGrade = calculatePassingGrade(questions.size());
            
            // Create and populate ExamContent
            ExamContent examContent = new ExamContent();
            examContent.setQuestions(questions);
            examContent.setAnswers(answers);
            examContent.setPassingGrade(passingGrade);
            
            return examContent;
            
        } catch (Exception e) {
            log.error("Error creating exam content: {}", e.getMessage());
            throw new TaskCreationException("Failed to create exam content");
        }
    }
    
    private Integer calculatePassingGrade(int numberOfQuestions) {
        // Example passing grade calculation:
        // - Base passing percentage is 60%
        // - Round up to nearest whole number
        return (int) Math.ceil(numberOfQuestions * 0.6);
    }

    private ExamContent saveExamContent(ExamContent examContent) {
        return examContentRepository.save(examContent);
    }
}