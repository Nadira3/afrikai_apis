package com.precious.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTests {
    // @Autowired
    // TestRestTemplate restTemplate;

    // @Test
    // void shouldReturnAUserWhenDataIsSaved() {
    //     ResponseEntity<String> response = restTemplate.getForEntity("api/users/1", String.class);

    //     assertEquals(response.getStatusCode(), HttpStatus.OK);
    // }

}