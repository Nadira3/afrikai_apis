package com.precious.UserApi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "This is the request object for authentication for user login")
public class AuthRequest {
	@Schema(description = "This is the email of the user")
	private String email;

	@Schema(description = "This is the password of the user")
	private String password;
}
