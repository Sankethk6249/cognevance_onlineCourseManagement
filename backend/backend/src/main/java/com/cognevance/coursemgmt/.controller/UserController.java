package com.cognevance.coursemgmt.controller;

import com.cognevance.coursemgmt.dto.Dtos.UserDto;
import com.cognevance.coursemgmt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/users") @RequiredArgsConstructor
public class UserController {
    private final UserRepository users;

    @GetMapping @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> all() {
        return users.findAll().stream()
                .map(u -> new UserDto(u.getId(), u.getName(), u.getEmail(), u.getRole().name())).toList();
    }
}