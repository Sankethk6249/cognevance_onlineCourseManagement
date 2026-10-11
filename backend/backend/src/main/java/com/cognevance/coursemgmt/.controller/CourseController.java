package com.cognevance.coursemgmt.controller;

import com.cognevance.coursemgmt.model.Course;
import com.cognevance.coursemgmt.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController @RequestMapping("/api/courses") @RequiredArgsConstructor
public class CourseController {
    private final CourseRepository courses;
    private final EnrollmentRepository enrollments;

    private static String blankToNull(String s) { return s == null || s.isBlank() ? null : s.trim(); }

    @GetMapping
    public List<Course> list(@RequestParam(required = false) String search,
                             @RequestParam(required = false) String category,
                             @RequestParam(required = false) String level) {
        return courses.filter(blankToNull(search), blankToNull(category), blankToNull(level));
    }

    @GetMapping("/{id}")
    public Course get(@PathVariable Long id) {
        return courses.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    }

    @PostMapping @PreAuthorize("hasRole('ADMIN')") @ResponseStatus(HttpStatus.CREATED)
    public Course create(@Valid @RequestBody Course c) {
        c.setId(null);
        return courses.save(c);
    }

    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public Course update(@PathVariable Long id, @Valid @RequestBody Course in) {
        Course c = get(id);
        c.setTitle(in.getTitle()); c.setDescription(in.getDescription()); c.setCategory(in.getCategory());
        c.setLevel(in.getLevel()); c.setInstructor(in.getInstructor()); c.setTotalLessons(in.getTotalLessons());
        return courses.save(c);
    }

    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") @Transactional
    public void delete(@PathVariable Long id) {
        get(id);
        enrollments.deleteByCourseId(id);
        courses.deleteById(id);
    }
}