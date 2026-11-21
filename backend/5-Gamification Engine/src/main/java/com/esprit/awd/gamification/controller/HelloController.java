package com.esprit.awd.gamification.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gamification")
public class HelloController {

    @Operation(summary = "Hello gamification service")
    @GetMapping("/hello")
    public String hello() {
        return "Hello gamification service";
    }
}