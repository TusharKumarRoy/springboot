package com.tusharkumarroy.studentmanagement.service;

import com.tusharkumarroy.studentmanagement.entity.Role;
import com.tusharkumarroy.studentmanagement.entity.User;
import com.tusharkumarroy.studentmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testStudent;
    private User testTeacher;

    @BeforeEach
    void setUp() {
        testStudent = new User();
        testStudent.setId(1L);
        testStudent.setUsername("john_student");
        testStudent.setEmail("john@student.com");
        testStudent.setPassword("password123");
        testStudent.setDepartment("Computer Science");
        testStudent.setRole(Role.STUDENT);

        testTeacher = new User();
        testTeacher.setId(2L);
        testTeacher.setUsername("jane_teacher");
        testTeacher.setEmail("jane@teacher.com");
        testTeacher.setPassword("password123");
        testTeacher.setDepartment("Mathematics");
        testTeacher.setRole(Role.TEACHER);
    }

    @Test
    void createUser_Success() {
        // Arrange
        when(userRepository.existsByUsername(testStudent.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(testStudent.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testStudent);

        // Act
        User createdUser = userService.createUser(testStudent);

        // Assert
        assertNotNull(createdUser);
        assertEquals(testStudent.getUsername(), createdUser.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_DuplicateUsername_ThrowsException() {
        // Arrange
        when(userRepository.existsByUsername(testStudent.getUsername())).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.createUser(testStudent));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_Success() {
        // Arrange
        User updatedDetails = new User();
        updatedDetails.setUsername("john_updated");
        updatedDetails.setEmail("john.new@student.com");
        updatedDetails.setDepartment("Physics");
        updatedDetails.setRole(Role.STUDENT);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(userRepository.existsByEmail(updatedDetails.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testStudent);

        // Act
        User result = userService.updateUser(1L, updatedDetails);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void assignStudentToTeacher_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testTeacher));
        when(userRepository.save(any(User.class))).thenReturn(testStudent);

        // Act
        User result = userService.assignStudentToTeacher(1L, 2L);

        // Assert
        assertNotNull(result);
        assertEquals(testTeacher, result.getAssignedTeacher());
        verify(userRepository, times(1)).save(testStudent);
    }

    @Test
    void deleteUser_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        doNothing().when(userRepository).deleteById(any(Long.class));

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).deleteById(1L);
    }
}
