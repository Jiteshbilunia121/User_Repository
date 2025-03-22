package org.example.controller;


import org.example.Entity.User;
import org.example.UserService.UserService;
import org.example.dto.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.example.util.JwtUtil;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserController(PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }


    // User Registration API
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (userService.getUserByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered!");
        }
        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    // Get User by Email API
    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userService.getUserByEmail(loginRequest.getUserEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(loginRequest.getUserPassword(), user.getPassword())) {
                String token = jwtUtil.generateToken(user.getEmail());
                return ResponseEntity.ok(token);
            }
        }
        return ResponseEntity.badRequest().body("Invalid email or password");
    }
}
