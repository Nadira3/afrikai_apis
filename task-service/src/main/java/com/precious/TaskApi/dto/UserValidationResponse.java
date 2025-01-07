package com.precious.TaskApi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserValidationResponse {
    private Long userId;
    private String username;
    private String role;
    private String token;
    private boolean isValid;
}