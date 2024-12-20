package com.precious.LabelAPI.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;

// Exception for when a file cannot be processed
@ExceptionHandler
public class FileValidationException extends RuntimeException {
    public FileValidationException(String message) {
        super(message);
    }
}
