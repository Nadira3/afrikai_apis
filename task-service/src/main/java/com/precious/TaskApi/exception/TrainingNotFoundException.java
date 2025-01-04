package com.precious.TaskApi.exception;

public class TrainingNotFoundException extends RuntimeException {
    public TrainingNotFoundException(Long trainingId) {
        super("Training with ID " + trainingId + " not found");
    }

}
