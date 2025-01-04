package com.precious.LabelAPI.exceptions;

// Exception for when an external service fails
public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String service, String message) {
        super("Error calling " + service + ": " + message);
    }
}
