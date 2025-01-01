package com.precious.UserApi.model.user;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.precious.UserApi.dto.user.UserResponseDto;
import com.precious.UserApi.model.enums.UserRole;

import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Entity;


@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; 

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.CLIENT;

    @Column(nullable = false)
    private BigDecimal wallet = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean enabled = false;

    @Column(nullable = false)
    private LocalDateTime registeredAt = LocalDateTime.now();

    // Custom constructor for user creation
    public User(String username, String email, String password, UserRole role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public UserResponseDto toUserResponseDto()
    {
        return new UserResponseDto(this.id, this.username, this.email);
    }
}
