package com.precious.LabelAPI.exceptions;

// Exception for when a file cannot be processed
public class FileValidationException extends RuntimeException {
    public FileValidationException(String message) {
        super(message);
    }
}
