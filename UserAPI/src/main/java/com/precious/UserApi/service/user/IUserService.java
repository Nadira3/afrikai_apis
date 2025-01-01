package com.precious.UserApi.service.user;

import com.precious.UserApi.dto.user.UserRegistrationDto;
import com.precious.UserApi.dto.user.UserResponseDto;
import com.precious.UserApi.dto.user.UserRequestDto;
import com.precious.UserApi.model.enums.UserRole;
import com.precious.UserApi.model.user.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService {
    // User Registration
    String registerUser(UserRegistrationDto registrationDto);

    // User Management
    User getUserById(Long id);
    UserResponseDto updateUser(Long userId, UserRequestDto userRequestDto);
    void deleteUser(Long userId);
    Page<User> getAllUsers(Pageable pageable);

    // Role Management
    void changeUserRole(Long userId, UserRole newRole);

    // Wallet Operations
    void addFundsToWallet(Long userId, double amount);
    void withdrawFundsFromWallet(Long userId, double amount);
}
