package com.tusharkumarroy.studentmanagement.integration;

import com.tusharkumarroy.studentmanagement.dto.RegisterRequest;
import com.tusharkumarroy.studentmanagement.entity.Role;
import com.tusharkumarroy.studentmanagement.entity.User;
import com.tusharkumarroy.studentmanagement.repository.UserRepository;
import com.tusharkumarroy.studentmanagement.service.AuthService;
import com.tusharkumarroy.studentmanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BasicIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void fullWorkflow_CreateUsersAndAssignTeacher() {
        // Register admin
        RegisterRequest adminRequest = new RegisterRequest();
        adminRequest.setUsername("admin");
        adminRequest.setEmail("admin@test.com");
        adminRequest.setPassword("admin123");
        adminRequest.setDepartment("IT");
        adminRequest.setRole(Role.ADMIN);

        User admin = authService.register(adminRequest);
        assertNotNull(admin);
        assertEquals("admin", admin.getUsername());

        // Create teacher
        User teacher = new User();
        teacher.setUsername("teacher1");
        teacher.setEmail("teacher1@test.com");
        teacher.setPassword("pass123");
        teacher.setDepartment("Computer Science");
        teacher.setRole(Role.TEACHER);

        User createdTeacher = userService.createUser(teacher);
        assertNotNull(createdTeacher.getId());

        // Create student
        User student = new User();
        student.setUsername("student1");
        student.setEmail("student1@test.com");
        student.setPassword("pass123");
        student.setDepartment("Computer Science");
        student.setRole(Role.STUDENT);

        User createdStudent = userService.createUser(student);
        assertNotNull(createdStudent.getId());

        // Assign student to teacher
        userService.assignStudentToTeacher(createdStudent.getId(), createdTeacher.getId());

        // Verify assignment
        User assignedStudent = userRepository.findById(createdStudent.getId()).orElseThrow();
        assertNotNull(assignedStudent.getAssignedTeacher());
        assertEquals("teacher1", assignedStudent.getAssignedTeacher().getUsername());

        // Verify counts
        List<User> allStudents = userService.getUsersByRole(Role.STUDENT);
        List<User> allTeachers = userService.getUsersByRole(Role.TEACHER);
        assertEquals(1, allStudents.size());
        assertEquals(1, allTeachers.size());
    }

    @Test
    void updateUser_WithoutPassword_ShouldWork() {
        // Create user
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPassword("password123");
        user.setDepartment("IT");
        user.setRole(Role.STUDENT);

        User created = userService.createUser(user);

        // Update without password
        created.setDepartment("Mathematics");
        created.setPassword(null); // No password update

        User updated = userService.updateUser(created.getId(), created);
        assertEquals("Mathematics", updated.getDepartment());
        assertEquals("testuser", updated.getUsername());
    }
}
