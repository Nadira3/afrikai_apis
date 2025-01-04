package com.precious.UserApi.controller;

import com.precious.UserApi.dto.user.UserRequestDto;
import com.precious.UserApi.dto.user.UserResponseDto;
import com.precious.UserApi.model.enums.UserRole;
import com.precious.UserApi.model.user.User;
import com.precious.UserApi.service.user.UserService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get all users with pagination and sorting
    @GetMapping({"", "/"})
    public ResponseEntity<List<UserResponseDto>> getAllUsers(Pageable pageable) {
        Page<User> userPage = userService.getAllUsers(pageable);

        // Convert User entities to UserDto
        Page<UserResponseDto> userDtoPage = userPage.map(User::toUserResponseDto);

        return ResponseEntity.ok(userDtoPage.getContent());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId) {
        UserResponseDto userDto = userService.getUserById(userId).toUserResponseDto();
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(
        @PathVariable Long userId,
        @RequestBody UserRequestDto userRequestDto
    ) {
        UserResponseDto updatedUserDto = userService.updateUser(userId, userRequestDto);
        return new ResponseEntity<>(updatedUserDto, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<Void> changeUserRole(
        @PathVariable Long userId, 
        @RequestParam UserRole newRole
    ) {
        userService.changeUserRole(userId, newRole);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/wallet/add")
    public ResponseEntity<Void> addFundsToWallet(
        @PathVariable Long userId, 
        @RequestParam double amount
    ) {
        userService.addFundsToWallet(userId, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/wallet/withdraw")
    public ResponseEntity<Void> withdrawFundsFromWallet(
        @PathVariable Long userId, 
        @RequestParam double amount
    ) {
        userService.withdrawFundsFromWallet(userId, amount);
        return ResponseEntity.ok().build();
    }
}