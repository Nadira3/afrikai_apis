package com.precious.TaskApi.exception;

public class ExamNotFoundException extends RuntimeException {
    public ExamNotFoundException(Long examId) {
        super("Exam not found: " + examId);
    }

}
