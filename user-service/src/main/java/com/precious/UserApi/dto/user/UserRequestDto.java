package com.precious.UserApi.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "User Request Data Transfer Object for user update")
public class UserRequestDto {
    @Schema(description = "Username of the user", example = "john_doe")
    private String username;

    @Schema(description = "Email of the user", example = "johndoe@example.com")
    private String email;

    @Schema(description = "Password of the user", example = "password")
    private String password;
}
