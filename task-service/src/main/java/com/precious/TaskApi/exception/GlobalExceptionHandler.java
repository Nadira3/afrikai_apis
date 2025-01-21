package com.precious.TaskApi.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.precious.TaskApi.dto.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

     // Generic method to create ErrorResponse
     private ResponseEntity<ErrorResponse> buildErrorResponse(Exception ex, HttpStatus status) {
        ErrorResponse error = new ErrorResponse(
            status.value(), 
            ex.getMessage(), 
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, status);
    }
	@ExceptionHandler(TaskNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ErrorResponse> handleTaskNotFoundException(TaskNotFoundException ex) {
		log.error("Task not found", ex);
		return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(TaskPopulationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleTaskPopulationException(TaskPopulationException ex) {
		log.error("Error populating task", ex);
		return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(TaskCreationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleTaskCreationException(TaskCreationException ex) {
		log.error("Error creating task", ex);
		return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(TaskProcessingException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleTaskProcessingException(TaskCreationException ex) {
		log.error("Error processing task", ex);
		return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(StorageException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorResponse> handleStorageException(StorageException ex) {
		log.error("Error storing file", ex);
		return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ErrorResponse> handleStorageFileNotFoundException(StorageFileNotFoundException ex) {
		log.error("File not found", ex);
		return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidTaskRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleInvalidTaskRequestException(InvalidTaskRequestException ex) {
		log.error("Invalid task request", ex);
		return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidTaskTransitionException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ResponseEntity<ErrorResponse> handleInvalidTaskTransitionException(InvalidTaskTransitionException ex) {
		log.error("Invalid task transition", ex);
		return buildErrorResponse(ex, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
		log.error("Illegal argument", ex);
		return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
		log.error("Unauthorized Action", ex);
		return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
		log.error("Error occurred", ex);
		return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}


