package com.precious.UserApi.dto.user;

import com.precious.UserApi.model.enums.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// User Creation DTO

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationDto {
    @NotBlank(message = "Username is required")
    private String username;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private UserRole role = UserRole.CLIENT;  // Default role
}
