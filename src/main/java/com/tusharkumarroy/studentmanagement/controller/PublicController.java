package com.tusharkumarroy.studentmanagement.controller;

import com.tusharkumarroy.studentmanagement.entity.Role;
import com.tusharkumarroy.studentmanagement.entity.User;
import com.tusharkumarroy.studentmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@PreAuthorize("isAuthenticated()")
public class PublicController {
    
    @Autowired
    private UserService userService;
    
    // Read-only endpoints for all authenticated users
    
    @GetMapping("/students")
    public ResponseEntity<List<User>> getAllStudents() {
        return ResponseEntity.ok(userService.getUsersByRole(Role.STUDENT));
    }
    
    @GetMapping("/teachers")
    public ResponseEntity<List<User>> getAllTeachers() {
        return ResponseEntity.ok(userService.getUsersByRole(Role.TEACHER));
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
