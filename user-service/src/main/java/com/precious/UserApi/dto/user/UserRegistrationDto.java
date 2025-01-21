package com.precious.UserApi.dto.user;

import com.precious.UserApi.model.enums.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// User Registration DTO

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User Registration data transfer object containing user registration details")
public class UserRegistrationDto {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Schema(description = "Username of the user", example = "john_doe")
    private String username;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Schema(description = "Email of the user", example = "johndoe@example.com")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(description = "Password of the user", example = "password123")
    private String password;

    @Schema(description = "Role of the user", example = "ADMIN", defaultValue = "CLIENT")
    private UserRole role = UserRole.CLIENT;
}
