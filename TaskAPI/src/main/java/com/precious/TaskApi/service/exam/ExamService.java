package com.precious.TaskApi.service.exam;

import com.precious.TaskApi.dto.task.TaskCreationDto;
import com.precious.TaskApi.exception.InvalidTaskRequestException;
import com.precious.TaskApi.exception.TaskCreationException;
import com.precious.TaskApi.model.content.ExamContent;
import com.precious.TaskApi.model.task.Exam;
import com.precious.TaskApi.repository.ExamRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExamService {
        
    private static final int MAX_QUESTIONS = 20;

    private final ExamRepository examRepository;
    private final ExamContentService examContentService;
    
    private void validateExamRequest(TaskCreationDto request) {
        // Validate exam request
        if (request.getQuestionNumber() <= 0) {
            throw new InvalidTaskRequestException("Number of questions must be a positive integer");
        }

        if (request.getQuestionNumber() > MAX_QUESTIONS) {
            throw new InvalidTaskRequestException("Number of questions exceeds the allowed maximum");
        }
        
        // Validate client ID (optional)
        if (request.getClientId() == null || request.getClientId().isEmpty()) {
            throw new InvalidTaskRequestException("Client ID is required");
        }
    }

    @Transactional
    public Exam createExam(TaskCreationDto request) {
        // validate exam request
        try {
            validateExamRequest(request);
            
        } catch (Exception e) {
            log.error("Error creating exam: {}", e.getMessage());
            throw new TaskCreationException("Error creating exam");
        }

        // Create exam content
        ExamContent content = examContentService.createAndSaveExamContent(request);

        // Create exam object
        Exam exam = new Exam();
        exam.setExamContent(content);
        
        return examRepository.save(exam);
    }
}
