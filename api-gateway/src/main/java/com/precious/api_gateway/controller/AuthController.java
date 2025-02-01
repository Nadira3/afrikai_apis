package com.precious.api_gateway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Refers to login.html in templates/
    }

}
