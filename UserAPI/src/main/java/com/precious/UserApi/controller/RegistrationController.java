package com.precious.UserApi.controller;


import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;

import com.precious.UserApi.dto.user.UserRegistrationDto;
import com.precious.UserApi.service.RegistrationService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("api/users/register")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public String register(@Valid @RequestBody UserRegistrationDto request) {
        return registrationService.register(request);
    }

    @GetMapping(path = "confirm")
    public String confirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }

}