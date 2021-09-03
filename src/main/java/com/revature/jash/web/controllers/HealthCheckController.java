package com.revature.jash.web.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@CrossOrigin
public class HealthCheckController {

    @GetMapping(produces = "application/json")
    public String health() {
        return "{\"status\": \"UP\"}";
    }

}
