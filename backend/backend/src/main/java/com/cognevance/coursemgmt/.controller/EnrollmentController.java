package com.cognevance.coursemgmt.controller;

import com.cognevance.coursemgmt.dto.Dtos.*;
import com.cognevance.coursemgmt.model.*;
import com.cognevance.coursemgmt.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController @RequestMapping("/api/enrollments") @RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentRepository enrollments;
    private final CourseRepository courses;
    private final UserRepository users;

    private User me(Authentication a) {
        return users.findByEmail(a.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private Enrollment mine(Authentication a, Long courseId) {
        return enrollments.findByUserIdAndCourseId(me(a).getId(), courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not enrolled in this course"));
    }

    private EnrollmentDto dto(Enrollment e) {
        int total = e.getCourse().getTotalLessons();
        int progress = total == 0 ? 0 : Math.round(e.getCompletedLessons() * 100f / total);
        return new EnrollmentDto(e.getId(), e.getCourse(), e.getUser().getName(), e.getUser().getEmail(),
                e.getCompletedLessons(), progress, e.getEnrolledAt());
    }

    @GetMapping("/my")
    public List<EnrollmentDto> my(Authentication a) {
        return enrollments.findByUserId(me(a).getId()).stream().map(this::dto).toList();
    }

    @GetMapping @PreAuthorize("hasRole('ADMIN')")
    public List<EnrollmentDto> all() {
        return enrollments.findAll().stream().map(this::dto).toList();
    }

    @PostMapping("/{courseId}") @PreAuthorize("hasRole('STUDENT')") @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentDto enroll(@PathVariable Long courseId, Authentication a) {
        User u = me(a);
        Course c = courses.findById(courseId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        if (enrollments.findByUserIdAndCourseId(u.getId(), courseId).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already enrolled in this course");
        Enrollment e = new Enrollment();
        e.setUser(u); e.setCourse(c);
        return dto(enrollments.save(e));
    }

    @DeleteMapping("/{courseId}") @PreAuthorize("hasRole('STUDENT')")
    public void unenroll(@PathVariable Long courseId, Authentication a) {
        enrollments.delete(mine(a, courseId));
    }

    @PutMapping("/{courseId}/progress") @PreAuthorize("hasRole('STUDENT')")
    public EnrollmentDto progress(@PathVariable Long courseId, @Valid @RequestBody ProgressRequest r, Authentication a) {
        Enrollment e = mine(a, courseId);
        if (r.completedLessons() > e.getCourse().getTotalLessons())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Completed lessons cannot exceed total lessons");
        e.setCompletedLessons(r.completedLessons());
        return dto(enrollments.save(e));
    }
}