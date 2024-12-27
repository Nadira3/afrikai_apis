package com.precious.UserApi.exception;

// Custom exception for user registration
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
