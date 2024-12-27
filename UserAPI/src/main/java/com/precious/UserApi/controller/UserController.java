package com.precious.UserApi.controller;

import com.precious.UserApi.dto.user.UserCreationDto;
import com.precious.UserApi.dto.user.UserRegistrationDto;
import com.precious.UserApi.dto.user.UserResponseDto;
import com.precious.UserApi.model.enums.UserRole;
import com.precious.UserApi.model.user.User;
import com.precious.UserApi.service.user.UserService;

import jakarta.validation.Valid;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
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
        Page<UserResponseDto> userDtoPage = userPage
                                                .map(user -> new UserResponseDto(user.getId(), user.getUsername(), user.getEmail()));

        return ResponseEntity.ok(userDtoPage.getContent());
    }

    // Register user and return the location of the new user
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(
        @Valid @RequestBody UserRegistrationDto registrationDto,
        UriComponentsBuilder uriComponentsBuilder
    ) {
        UserResponseDto registeredUserDto = userService.registerUser(registrationDto);
        return ResponseEntity.created(uriComponentsBuilder.path("/api/users/{id}")
            .buildAndExpand(registeredUserDto.getId()).toUri()).body(registeredUserDto);
    }


    @PostMapping("/admin/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> createUser(
        @RequestBody UserCreationDto creationDto
    ) {
        UserResponseDto createdUserDto = userService.createUser(creationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId) {
        UserResponseDto userDto = userService.getUserById(userId).toUserResponseDto();
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(
        @PathVariable Long userId,
        @RequestBody User userDetails
    ) {
        UserResponseDto updatedUserDto = userService.updateUser(userId, userDetails);
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