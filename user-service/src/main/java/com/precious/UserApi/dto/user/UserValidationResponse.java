package com.precious.UserApi.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserValidationResponse {
    private Long userId;
    private String role;
    private String token;
    private boolean isValid;
}
