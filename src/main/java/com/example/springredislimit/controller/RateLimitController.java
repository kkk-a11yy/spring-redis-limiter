package com.example.springredislimit.controller;


import com.example.springredislimit.limitService.RateLimiterService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
public class RateLimitController {

    private final RateLimiterService rateLimiterService;

    @Autowired
    public RateLimitController(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;    }

    @GetMapping("/api/resource")
    public String getResource() {
        String userId = "3"; // 在实际应用中，您可能需要从请求中提取用户ID
        if (rateLimiterService.allowRequest(userId, 5, Duration.ofSeconds(30))) {
            return "Request allowed";
        } else {
            return "Too many requests - try again later";
        }
    }
    @PostMapping("/api/postresource")
    public String postResource() {
        String userId = "3"; // 在实际应用中，您可能需要从请求中提取用户ID

        if (rateLimiterService.allowRequest(userId, 5, Duration.ofSeconds(30))) {
            return "Request allowed";
        } else {
            return "Too many requests - try again later";
        }
    }

    @PutMapping("/api/getresource")
    public String putResource() {
        String userId = "3"; // 在实际应用中，您可能需要从请求中提取用户ID

        if (rateLimiterService.allowRequest(userId, 5, Duration.ofSeconds(30))) {
            return "Request allowed";
        } else {
            return "Too many requests - try again later";
        }
    }
}