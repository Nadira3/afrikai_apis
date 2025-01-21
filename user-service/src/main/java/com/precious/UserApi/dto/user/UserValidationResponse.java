package com.precious.UserApi.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "User validation response; this is the response object that is returned when a user is validated")
public class UserValidationResponse {
	@Schema(description = "The unique id of the user that was validated")
	private Long userId;

	@Schema(description = "The role of the user that was validated necessary for authorization")
	private String role;

	@Schema(description = "The token that was validated")
	private String token;

	@Schema(description = "The validity of the token")
	private boolean isValid;

	@Schema(description = "The status of the user; Users are enabled after verifying their email")
	private boolean isEnabled;
}
