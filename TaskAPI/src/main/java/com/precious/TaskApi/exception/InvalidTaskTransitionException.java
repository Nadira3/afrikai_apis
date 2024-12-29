package com.precious.TaskApi.exception;

public class InvalidTaskTransitionException extends RuntimeException {
    public InvalidTaskTransitionException(String message) {
        super(message);
    }

}
