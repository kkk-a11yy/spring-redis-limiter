package com.example.springredislimit.controller;


import com.example.springredislimit.limitService.RateLimiterService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

//    @GetMapping("/{userId}/{api}")
    @GetMapping("/api/getresources2")
    public ResponseEntity<String> getApi(@PathVariable String userId, @PathVariable String api) {
        if (!rateLimiterService.isAllowed(userId, api, 10000, 60)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded");
        }
        return ResponseEntity.ok("API response for " + api);
    }

}