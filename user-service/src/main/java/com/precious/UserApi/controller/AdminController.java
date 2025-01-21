package com.precious.UserApi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/users/admin")
@Tag(name = "Other", description = "Admin related endpoints")
public class AdminController {

    // Get all users with pagination and sorting
    @GetMapping({"", "/"})
    public String getAllUsers() {

        return "All users";
    }
}
