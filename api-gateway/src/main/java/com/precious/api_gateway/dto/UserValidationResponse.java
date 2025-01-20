package com.precious.api_gateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response containing user validation details")
public class UserValidationResponse {
	@Schema(description = "User id associated with the token")
	private Long userId;

	@Schema(description = "User role associated with the token")
	private String role;

	@Schema(description = "Token to be used for authentication")
	private String token;
	
	@Schema(description = "Flag to indicate if the user is valid")
	private boolean isValid;

	@Schema(description = "Flag to indicate if the user is enabled")
	private boolean isEnabled;
}
