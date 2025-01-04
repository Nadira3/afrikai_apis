package com.precious.TaskApi.exception;

public class InvalidTaskRequestException extends RuntimeException {
    public InvalidTaskRequestException(String message) {
        super(message);
    }

}
