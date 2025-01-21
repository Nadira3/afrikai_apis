package com.precious.UserApi.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "User details response object encapsulating sensitive user attributes")
public class UserResponseDto {
	@Schema(description = "Unique identifier of the user", example = "123456")
	private Long id;

	@Schema(description = "Username of the user", example = "john_doe")
	private String username;

	@Schema(description = "Email of the user", example = "johndoe@example.com")
	private String email;
}
