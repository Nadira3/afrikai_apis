package com.precious.LabelAPI.exceptions;

// Exception for when a file cannot be processed
public class FailedSubmissionException extends RuntimeException {
    public FailedSubmissionException(String message) {
        super(message);
    }
}
