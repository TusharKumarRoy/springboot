package com.tusharkumarroy.studentmanagement.config;

import com.tusharkumarroy.studentmanagement.entity.Role;
import com.tusharkumarroy.studentmanagement.entity.User;
import com.tusharkumarroy.studentmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (userRepository.count() > 0) {
            System.out.println("Data already exists. Skipping data initialization.");
            return;
        }

        System.out.println("Loading dummy data...");

        // Create admin user
        User admin = createUser("admin", "admin@example.com", "admin123", "Administration", Role.ADMIN);

        // Create teachers
        User teacher1 = createUser("john_doe", "john.doe@school.com", "teacher123", "Mathematics", Role.TEACHER);
        User teacher2 = createUser("jane_smith", "jane.smith@school.com", "teacher123", "Physics", Role.TEACHER);
        User teacher3 = createUser("bob_wilson", "bob.wilson@school.com", "teacher123", "Computer Science", Role.TEACHER);

        // Create students
        User student1 = createUser("alice_brown", "alice.brown@student.com", "student123", "Computer Science", Role.STUDENT);
        User student2 = createUser("charlie_davis", "charlie.davis@student.com", "student123", "Mathematics", Role.STUDENT);
        User student3 = createUser("emma_miller", "emma.miller@student.com", "student123", "Physics", Role.STUDENT);
        User student4 = createUser("david_lee", "david.lee@student.com", "student123", "Computer Science", Role.STUDENT);
        User student5 = createUser("sophia_taylor", "sophia.taylor@student.com", "student123", "Mathematics", Role.STUDENT);

        // Assign students to teachers
        student1.setAssignedTeacher(teacher3); // Alice -> Bob (CS)
        student2.setAssignedTeacher(teacher1); // Charlie -> John (Math)
        student3.setAssignedTeacher(teacher2); // Emma -> Jane (Physics)
        student4.setAssignedTeacher(teacher3); // David -> Bob (CS)
        student5.setAssignedTeacher(teacher1); // Sophia -> John (Math)

        userRepository.save(student1);
        userRepository.save(student2);
        userRepository.save(student3);
        userRepository.save(student4);
        userRepository.save(student5);

        System.out.println("Dummy data loaded successfully!");
        System.out.println("Admin: admin / admin123");
        System.out.println("Teachers: john_doe, jane_smith, bob_wilson (password: teacher123)");
        System.out.println("Students: alice_brown, charlie_davis, emma_miller, david_lee, sophia_taylor (password: student123)");
    }

    private User createUser(String username, String email, String password, String department, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setDepartment(department);
        user.setRole(role);
        return userRepository.save(user);
    }
}
