package com.precious.UserApi.controller;

import com.precious.UserApi.dto.user.UserRequestDto;
import com.precious.UserApi.dto.user.UserResponseDto;
import com.precious.UserApi.model.enums.UserRole;
import com.precious.UserApi.model.user.User;
import com.precious.UserApi.service.user.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

import jakarta.validation.Valid;

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
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
	summary = "Get all users",
	description = "Fetch all users with their details in a paginated manner",
	responses = {
	    @ApiResponse(
	        responseCode = "200", description = "Users fetched successfully",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))
	    )
	}
    )
    @GetMapping({"", "/"})
    public ResponseEntity<List<UserResponseDto>> getAllUsers(Pageable pageable) {
        Page<User> userPage = userService.getAllUsers(pageable);

        // Convert User entities to UserDto
        Page<UserResponseDto> userDtoPage = userPage.map(User::toUserResponseDto);

        return ResponseEntity.ok(userDtoPage.getContent());
    }

    @Operation(
        summary = "Get user details by ID",
	description = "Fetch user details by providing a valid user ID",
	responses = {
		@ApiResponse(
		    responseCode = "200",
		    description = "User details fetched successfully",
		    content = @Content(
			    mediaType = "application/json",
			    schema = @Schema(implementation = UserResponseDto.class),
			    examples = @ExampleObject(
				    name = "User Example",
				    value = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"johndoe@example.com\"}"
			    )
		    )
		),
        
		@ApiResponse(
		    responseCode = "404",
                    description = "User not found"
       		)
	}
    )
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable @Parameter(
			    	description = "User ID",
				required = true,
				schema = @Schema(type = "integer", format = "int64")
			    ) Long userId) {
        UserResponseDto userDto = userService.getUserById(userId).toUserResponseDto();
	if (userDto != null) {
		return new ResponseEntity<>(userDto, HttpStatus.OK);
	}
	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(
	summary = "Update a user object",
	description = "Update user details by providing a valid user ID and the updated user details",
	responses = {
	    @ApiResponse(
	        responseCode = "200", description = "User details updated successfully",
		content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = UserResponseDto.class),
			examples = @ExampleObject(
			    name = "User Example",
			    value = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"johndoe@example.com\"}"
			)
		)
	    ),
	    @ApiResponse(
	        responseCode = "404", description = "User not found"
	    )
	}
    )
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(
        @PathVariable @Parameter(
		description = "User ID",
		required = true,
		schema = @Schema(type = "integer", format = "int64")
	) Long userId,
        @Valid @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
	        description = "Updated user details",
		required = true,
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = UserRequestDto.class),
			examples = @ExampleObject(
				name = "User Update Example",
				value = """
						{

							"username": "Jane Doe",
							"email": "janedoe@example.com"
						}"""
			)
		)
	) UserRequestDto userRequestDto
    ) {
        UserResponseDto updatedUserDto = userService.updateUser(userId, userRequestDto);
	if (updatedUserDto == null) {
	    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
        return new ResponseEntity<>(updatedUserDto, HttpStatus.OK);
    }

    @Operation(
    	summary = "Delete a user",
	description = "Delete a user by providing a valid user ID",
	responses = {
	    @ApiResponse(
	        responseCode = "204", description = "User deleted successfully"
	    ),
	    @ApiResponse(
	        responseCode = "404", description = "User not found"
	    )
	}
    )
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Parameter(
			    	description = "User ID",
				required = true,
				schema = @Schema(type = "integer", format = "int64"
			)
			    ) Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
    	summary = "Change user role",
	description = "Change the role of a user by providing a valid user ID and the new role",
	responses = {
	    @ApiResponse(
	        responseCode = "200", description = "User role changed successfully"
	    ),
	    @ApiResponse(
	        responseCode = "404", description = "User not found"
	    )
	}
    )
    @PatchMapping("/{userId}/role")
    public ResponseEntity<Void> changeUserRole(
        @PathVariable @Parameter(
		description = "User ID",
		required = true,
		schema = @Schema(type = "integer", format = "int64")
	) Long userId, 
        @RequestParam @Parameter(
		description = "New role",
		required = true,
		example = "ADMIN",
		schema = @Schema(implementation = UserRole.class)
	) UserRole newRole
    ) {
        userService.changeUserRole(userId, newRole);
        return ResponseEntity.ok().build();
    }

    @Operation(
    	summary = "Add funds to wallet",
	description = "Add funds to the wallet of a user by providing a valid user ID and the amount to add",
	responses = {
	    @ApiResponse(
	        responseCode = "200", description = "Funds added to wallet successfully"
	    ),
	    @ApiResponse(
	        responseCode = "404", description = "User not found"
	    )
	}
    )
    @PostMapping("/{userId}/wallet/add")
    public ResponseEntity<Void> addFundsToWallet(
        @PathVariable @Parameter(
		description = "User ID",
		required = true,
		schema = @Schema(type = "integer", format = "int64")
	) Long userId, 
        @RequestParam @Parameter(
		description = "Amount to withdraw",
		required = true,
		schema = @Schema(type = "number", format = "double")
	) double amount
    ) {
        userService.addFundsToWallet(userId, amount);
        return ResponseEntity.ok().build();
    }

    @Operation(
    	summary = "Withdraw funds from wallet",
	description = "Withdraw funds from the wallet of a user by providing a valid user ID and the amount to withdraw",
	responses = {
	    @ApiResponse(
	        responseCode = "200", description = "Funds withdrawn from wallet successfully"
	    ),
	    @ApiResponse(
	        responseCode = "404", description = "User not found"
	    )
	}
    )
    @PostMapping("/{userId}/wallet/withdraw")
    public ResponseEntity<Void> withdrawFundsFromWallet(
        @PathVariable @Parameter(
		description = "User ID",
		required = true,
		schema = @Schema(type = "integer", format = "int64")
	) Long userId, 
        @RequestParam @Parameter(
		description = "Amount to withdraw",
		required = true,
		schema = @Schema(type = "number", format = "double")
	) double amount
    ) {
        userService.withdrawFundsFromWallet(userId, amount);
        return ResponseEntity.ok().build();
    }
}
