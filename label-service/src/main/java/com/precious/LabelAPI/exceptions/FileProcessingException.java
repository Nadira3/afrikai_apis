package com.precious.LabelAPI.exceptions;

// Exception for when a file cannot be processed
public class FileProcessingException extends RuntimeException {
    public FileProcessingException(String message) {
        super(message);
    }
}
