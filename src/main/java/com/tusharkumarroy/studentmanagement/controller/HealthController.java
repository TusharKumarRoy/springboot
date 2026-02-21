package com.tusharkumarroy.studentmanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {
    
    @GetMapping("/")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Student Management API is running");
        response.put("timestamp", LocalDateTime.now());
        response.put("endpoints", Map.of(
            "register", "POST /api/auth/register",
            "login", "POST /api/auth/login",
            "students", "GET /api/admin/students (requires ADMIN role)",
            "teachers", "GET /api/admin/teachers (requires ADMIN role)"
        ));
        return response;
    }
    
    @GetMapping("/health")
    public Map<String, String> simpleHealth() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        return response;
    }
}
