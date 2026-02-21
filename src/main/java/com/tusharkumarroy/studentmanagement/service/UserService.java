package com.tusharkumarroy.studentmanagement.service;

import com.tusharkumarroy.studentmanagement.entity.Role;
import com.tusharkumarroy.studentmanagement.entity.User;
import com.tusharkumarroy.studentmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    
    public List<User> getUnassignedStudents() {
        return userRepository.findByRoleAndAssignedTeacherIsNull(Role.STUDENT);
    }
    
    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        
        if (!user.getEmail().equals(userDetails.getEmail()) && 
            userRepository.existsByEmail(userDetails.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setDepartment(userDetails.getDepartment());
        user.setRole(userDetails.getRole());
        
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        
        return userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        
        // Unassign students if deleting a teacher
        if (user.getRole() == Role.TEACHER && user.getAssignedStudents() != null) {
            for (User student : user.getAssignedStudents()) {
                student.setAssignedTeacher(null);
                userRepository.save(student);
            }
        }
        
        userRepository.deleteById(id);
    }
    
    @Transactional
    public User assignStudentToTeacher(Long studentId, Long teacherId) {
        User student = getUserById(studentId);
        User teacher = getUserById(teacherId);
        
        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("User is not a student");
        }
        
        if (teacher.getRole() != Role.TEACHER) {
            throw new RuntimeException("User is not a teacher");
        }
        
        student.setAssignedTeacher(teacher);
        return userRepository.save(student);
    }
    
    @Transactional
    public User unassignStudentFromTeacher(Long studentId) {
        User student = getUserById(studentId);
        
        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("User is not a student");
        }
        
        student.setAssignedTeacher(null);
        return userRepository.save(student);
    }
}
