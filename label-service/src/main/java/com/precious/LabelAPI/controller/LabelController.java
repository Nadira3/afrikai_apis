package com.precious.LabelAPI.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/labels/user")
public class LabelController {

    @GetMapping("/")
    public String getHomePage() {
           return "Welcome to Label API";
    }

}
