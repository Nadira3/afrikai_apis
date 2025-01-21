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
@Schema(description = "This is the response object that is returned when a user logs in")
public class AuthResponse {

	@Schema(description = "This is the token containing encrypted user details that is returned when a user logs in successfully")
	private String token;

}
