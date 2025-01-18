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
@RequestMapping("/api/users/admin")
public class AdminController {

    // Get all users with pagination and sorting
    @GetMapping({"", "/"})
    public String getAllUsers() {

        return "All users";
    }
}
