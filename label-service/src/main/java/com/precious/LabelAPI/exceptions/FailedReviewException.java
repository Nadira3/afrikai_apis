package com.precious.LabelAPI.exceptions;

// Exception for when a file cannot be processed
public class FailedReviewException extends RuntimeException {
    public FailedReviewException(String message) {
        super(message);
    }
}
