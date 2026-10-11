package com.cognevance.coursemgmt.dto;

import com.cognevance.coursemgmt.model.Course;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class Dtos {
    public record RegisterRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Email is required") @Email(message = "Email is invalid") String email,
        @NotBlank @Size(min = 6, message = "Password must be at least 6 characters") String password) {}
    public record LoginRequest(@NotBlank String email, @NotBlank String password) {}
    public record AuthResponse(String token, String name, String email, String role) {}
    public record ProgressRequest(@Min(value = 0, message = "Completed lessons cannot be negative") int completedLessons) {}
    public record EnrollmentDto(Long id, Course course, String studentName, String studentEmail,
                                int completedLessons, int progress, LocalDateTime enrolledAt) {}
    public record UserDto(Long id, String name, String email, String role) {}
}