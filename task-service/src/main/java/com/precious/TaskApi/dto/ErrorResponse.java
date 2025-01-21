package com.precious.TaskApi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Error Response object that contains the status, message and timestamp of the error")
public class ErrorResponse {
	@Schema(description = "Status code of the error")
	private int status;

	@Schema(description = "Message of the error")
	private String message;

	@Schema(description = "Timestamp of the error")
	private LocalDateTime timestamp;
}
