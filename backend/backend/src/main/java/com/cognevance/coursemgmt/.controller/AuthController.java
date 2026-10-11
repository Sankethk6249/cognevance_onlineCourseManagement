package com.cognevance.coursemgmt.controller;

import com.cognevance.coursemgmt.dto.Dtos.*;
import com.cognevance.coursemgmt.model.User;
import com.cognevance.coursemgmt.repository.UserRepository;
import com.cognevance.coursemgmt.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest r) {
        String email = r.email().trim().toLowerCase();
        if (users.existsByEmail(email))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        User u = new User();
        u.setName(r.name().trim()); u.setEmail(email); u.setPassword(encoder.encode(r.password()));
        return response(users.save(u));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest r) {
        User u = users.findByEmail(r.email().trim().toLowerCase())
                .filter(x -> encoder.matches(r.password(), x.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        return response(u);
    }

    private AuthResponse response(User u) {
        return new AuthResponse(jwt.generate(u.getEmail(), u.getRole().name()), u.getName(), u.getEmail(), u.getRole().name());
    }
}