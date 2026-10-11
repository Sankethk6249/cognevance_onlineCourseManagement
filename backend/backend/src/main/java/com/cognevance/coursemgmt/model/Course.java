package com.cognevance.coursemgmt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity @Table(name = "courses")
@Getter @Setter @NoArgsConstructor
public class Course {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Title is required") private String title;
    @NotBlank(message = "Description is required") @Column(length = 2000) private String description;
    @NotBlank(message = "Category is required") private String category;
    @NotBlank(message = "Level is required") private String level;
    @NotBlank(message = "Instructor is required") private String instructor;
    @Min(value = 1, message = "Total lessons must be at least 1") private int totalLessons;
}