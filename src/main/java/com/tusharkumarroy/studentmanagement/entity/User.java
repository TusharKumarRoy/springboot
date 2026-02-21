package com.tusharkumarroy.studentmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Username is required")
    @Column(nullable = false, unique = true)
    private String username;
    
    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;
    
    @NotBlank(message = "Department is required")
    @Column(nullable = false)
    private String department;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    // For students: their assigned teacher
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id")
    @JsonIgnoreProperties({"password", "assignedStudents"})
    private User assignedTeacher;
    
    // For teachers: their assigned students
    @OneToMany(mappedBy = "assignedTeacher", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"password", "assignedTeacher"})
    private List<User> assignedStudents = new ArrayList<>();
    
    // Constructors
    public User() {}
    
    public User(String username, String password, String email, String department, Role role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.department = department;
        this.role = role;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public User getAssignedTeacher() {
        return assignedTeacher;
    }
    
    public void setAssignedTeacher(User assignedTeacher) {
        this.assignedTeacher = assignedTeacher;
    }
    
    public List<User> getAssignedStudents() {
        return assignedStudents;
    }
    
    public void setAssignedStudents(List<User> assignedStudents) {
        this.assignedStudents = assignedStudents;
    }
}
