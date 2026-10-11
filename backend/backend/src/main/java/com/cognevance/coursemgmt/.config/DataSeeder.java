package com.cognevance.coursemgmt.config;

import com.cognevance.coursemgmt.model.*;
import com.cognevance.coursemgmt.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component @RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository users;
    private final CourseRepository courses;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (users.count() == 0) {
            User admin = new User();
            admin.setName("Admin"); admin.setEmail("admin@course.com");
            admin.setPassword(encoder.encode("Admin@123")); admin.setRole(User.Role.ADMIN);
            users.save(admin);
        }
        if (courses.count() == 0) {
            courses.saveAll(List.of(
                c("Java Programming Masterclass", "Core Java, OOP, collections and streams from scratch.", "Programming", "BEGINNER", "Dr. Anita Rao", 24),
                c("Spring Boot REST APIs", "Build secure, production-ready REST APIs with Spring Boot and JWT.", "Programming", "INTERMEDIATE", "Rahul Menon", 18),
                c("React for Beginners", "Components, hooks, routing and state management.", "Programming", "BEGINNER", "Priya Shetty", 20),
                c("Data Science with Python", "Pandas, NumPy and machine learning basics.", "Data Science", "INTERMEDIATE", "Dr. Kiran Patel", 30),
                c("UI/UX Design Fundamentals", "Wireframes, prototyping and design thinking.", "Design", "BEGINNER", "Sneha Iyer", 12),
                c("DevOps with Docker & CI/CD", "Containers, pipelines and cloud deployment.", "DevOps", "ADVANCED", "Arjun Nair", 16)));
        }
    }

    private Course c(String title, String desc, String cat, String level, String instructor, int lessons) {
        Course c = new Course();
        c.setTitle(title); c.setDescription(desc); c.setCategory(cat);
        c.setLevel(level); c.setInstructor(instructor); c.setTotalLessons(lessons);
        return c;
    }
}