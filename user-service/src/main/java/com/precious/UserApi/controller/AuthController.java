package com.precious.UserApi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.precious.UserApi.dto.AuthRequest;
import com.precious.UserApi.dto.AuthResponse;
import com.precious.UserApi.dto.ErrorResponse;
import com.precious.UserApi.dto.user.UserRegistrationDto;
import com.precious.UserApi.dto.user.UserValidationResponse;
import com.precious.UserApi.service.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {

    private final AuthenticationService authenticationService;

    @Operation(
        summary = "Register a new user",
	description = "Register a new user with the provided details",
	requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
	    content = @Content(
		    mediaType = "application/json",
		    schema = @Schema(implementation = UserRegistrationDto.class),
		    examples = @ExampleObject(
			    name = "User Registration",
			    value = "{\n  \"username\": \"john_doe\",\n  \"email\": \"johndoe@example.com\",\n  \"password\": \"password\",\n \"role\": \"CLIENT\"}"
		    )
	    )
	),
	responses = {
	    @ApiResponse(
	        responseCode = "200", description = "User registered successfully",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "400", description = "Invalid request",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
	    )
	}
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegistrationDto request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @Operation(
	summary = "Confirm user registration",
	description = "Confirm user registration with the provided token",
	parameters = @Parameter(
	    name = "token",
	    description = "Token received in the email",
	    required = true
	),
	responses = {
	    @ApiResponse(
	        responseCode = "200", description = "User registration confirmed successfully",
		content = @Content(schema = @Schema(type = "string"))
	    ),

	    @ApiResponse(
	        responseCode = "400", description = "Invalid token",
		content = @Content(schema = @Schema(type = "string"))
	    )
	}
    )
    @GetMapping("/register/confirm")
    public String confirm(@RequestParam("token") String token) {
        return authenticationService.confirmToken(token);
    }

    @Operation(
    	summary = "Authenticate/Login a user",
	description = "Authenticate/Login a user with the provided credentials",
	requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
	    content = @Content(
		    mediaType = "application/json",
		    schema = @Schema(implementation = AuthRequest.class),
		    examples = @ExampleObject(
			    name = "User Authentication",
			    value = "{\n  \"username\": \"john_doe\",\n  \"password\": \"password\"}"
		    )
	    )
	),
	responses = {
	    @ApiResponse(
	        responseCode = "200", description = "User authenticated successfully",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "401", description = "Invalid credentials",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
	    )
	}
    )
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @Operation(
    	summary = "Validate user token",
	description = "Validate user token to check if it is still valid",
	responses = {
	    @ApiResponse(
	        responseCode = "200", description = "Token is valid",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserValidationResponse.class))
	    ),
	    @ApiResponse(
	        responseCode = "401", description = "Token is invalid",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
	    )
	}
    )
    @PostMapping("/validate")
    public ResponseEntity<UserValidationResponse> validateToken(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(authenticationService.validateToken(token));
    }

}
