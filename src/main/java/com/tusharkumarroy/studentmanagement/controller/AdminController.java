package com.tusharkumarroy.studentmanagement.controller;

import com.tusharkumarroy.studentmanagement.entity.Role;
import com.tusharkumarroy.studentmanagement.entity.User;
import com.tusharkumarroy.studentmanagement.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    @Autowired
    private UserService userService;
    
    // User Management
    
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/students")
    public ResponseEntity<?> getAllStudents() {
        try {
            List<User> students = userService.getUsersByRole(Role.STUDENT);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/teachers")
    public ResponseEntity<?> getAllTeachers() {
        try {
            List<User> teachers = userService.getUsersByRole(Role.TEACHER);
            return ResponseEntity.ok(teachers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            logger.info("=== UPDATE USER REQUEST ===");
            logger.info("User ID: " + id);
            logger.info("Authenticated: " + (auth != null && auth.isAuthenticated()));
            logger.info("Principal: " + (auth != null ? auth.getPrincipal() : "null"));
            logger.info("Authorities: " + (auth != null ? auth.getAuthorities() : "null"));
            
            User updatedUser = userService.updateUser(id, user);
            logger.info("User updated successfully");
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("Error updating user: " + e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Assignment Management
    
    @PutMapping("/assign/{studentId}/to/{teacherId}")
    public ResponseEntity<?> assignStudentToTeacher(@PathVariable Long studentId, @PathVariable Long teacherId) {
        try {
            User student = userService.assignStudentToTeacher(studentId, teacherId);
            return ResponseEntity.ok(student);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/unassign/{studentId}")
    public ResponseEntity<?> unassignStudentFromTeacher(@PathVariable Long studentId) {
        try {
            User student = userService.unassignStudentFromTeacher(studentId);
            return ResponseEntity.ok(student);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
