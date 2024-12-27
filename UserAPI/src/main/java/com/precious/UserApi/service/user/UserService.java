package com.precious.UserApi.service.user;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.precious.UserApi.dto.user.UserCreationDto;
import com.precious.UserApi.dto.user.UserRegistrationDto;
import com.precious.UserApi.dto.user.UserResponseDto;
import com.precious.UserApi.repository.UserRepository;
import com.precious.UserApi.exception.*;
import com.precious.UserApi.model.enums.UserRole;
import com.precious.UserApi.model.user.Admin;
import com.precious.UserApi.model.user.Client;
import com.precious.UserApi.model.user.Tasker;
import com.precious.UserApi.model.user.User;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(
        UserRepository userRepository, 
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        // Validate unique email and username
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            logger.warn("Attempted registration with existing email: {}", registrationDto.getEmail());
            throw new UserAlreadyExistsException("Email is already in use");
        }

        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            logger.warn("Attempted registration with existing username: {}", registrationDto.getUsername());
            throw new UserAlreadyExistsException("Username is already in use");
        }

        // Create new user
        User newUser = createUserByRole(
            registrationDto.getUsername(),
            registrationDto.getEmail(),
            registrationDto.getPassword(),
            registrationDto.getRole()
            );
        
        // Save and log
        User savedUser = userRepository.save(newUser);
        logger.info("New user registered: {} with role {}", savedUser.getUsername(), savedUser.getRole());
        
        return userRepository.save(newUser).toUserResponseDto();
    }

    @Override
    public UserResponseDto createUser(UserCreationDto creationDto) {
        // Create user with specified role
        if (!UserRole.exists(creationDto.getRole().name())) {
            throw new IllegalArgumentException("Invalid role: " + creationDto.getRole());
        }

        User newUser = createUserByRole(
            creationDto.getUsername(),
            creationDto.getEmail(),
            creationDto.getPassword(),
            creationDto.getRole()
            );   

        return userRepository.save(newUser).toUserResponseDto();
    }

    
    public User createUserByRole(String username, String email, String password, UserRole role) {
        // Create user with specified role

        // Check if role is valid
        if (!UserRole.exists(role.name())) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }

        User newUser;
        
        // Instantiate the user based on the role
        switch (role) {
            case CLIENT:
                newUser = new Client();
                break;
            case TASKER:
                newUser = new Tasker();
                break;
            case ADMIN:
                newUser = new Admin(); // Admin will have no special attributes
                break;
            default:   // Should never reach here
                throw new IllegalArgumentException("Invalid role: " + role);
        }
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(role);

        return newUser;
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public UserResponseDto updateUser(Long userId, User updatedUser) {
        return userRepository.findById(userId)
            .map(existingUser -> {
                existingUser.setUsername(updatedUser.getUsername());
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword())); // Hash the password
                existingUser.setRole(updatedUser.getRole()); // Update roles if needed
                return userRepository.save(existingUser).toUserResponseDto();
            })
            .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));
    }
    
    @Override
    public void deleteUser(Long userId) {
        try {
            User userToDelete = getUserById(userId);
            userRepository.delete(userToDelete);
        } catch (Exception e) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        logger.info("User with id: {} deleted", userId);
    }

    @Override
    public void changeUserRole(Long userId, UserRole newRole) {
        try {
            if (!UserRole.exists(newRole.name())) {
                throw new IllegalArgumentException("Invalid role: " + newRole);
            }
            User user = getUserById(userId);
            user.setRole(newRole);
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + newRole);
        } catch (Exception e) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        logger.info("User with Id: {} role changed to {}", userId, newRole);
    }

    private void validatePositiveAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }    

    // Wallet methods
    @Override
    public void addFundsToWallet(Long userId, double amount) {
        try {
            validatePositiveAmount(amount);
            User user = getUserById(userId);
            user.setWallet(user.getWallet().add(BigDecimal.valueOf(amount)));
            userRepository.save(user);
            logger.info("Added funds to user wallet: {} amount: {}", userId, amount);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Amount must be positive");
        } catch (Exception e) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }

    @Override
    public void withdrawFundsFromWallet(Long userId, double amount) {
        try {
            validatePositiveAmount(amount);
            User user = getUserById(userId);
            if (amount > 0 && user.getWallet().compareTo(BigDecimal.valueOf(amount)) >= 0) {
                user.setWallet(user.getWallet().subtract(BigDecimal.valueOf(amount)));
                userRepository.save(user);
                logger.info("Withdrawn funds from user wallet: {} amount: {}", userId, amount);
            } else {
                throw new InsufficientFundsException("Insufficient funds or invalid amount");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Amount must be positive");
        } catch (InsufficientFundsException e) {
            throw new InsufficientFundsException("Insufficient funds or invalid amount");
        } catch (Exception e) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }
}
