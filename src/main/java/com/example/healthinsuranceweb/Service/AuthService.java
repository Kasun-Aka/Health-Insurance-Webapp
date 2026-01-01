package com.example.healthinsuranceweb.Service;


import com.example.healthinsuranceweb.DTO.AuthResponse;
import com.example.healthinsuranceweb.DTO.LoginRequest;
import com.example.healthinsuranceweb.DTO.RegisterRequest;
import com.example.healthinsuranceweb.Entity.User;
import com.example.healthinsuranceweb.Repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = encoder;
    }

    // Register -> Map response (success/message)
    public Map<String, Object> register(RegisterRequest request) {
        if (request.getEmail() == null || request.getPassword() == null || request.getUsername() == null) {
            return Map.of("success", false, "message", "Missing fields");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return Map.of("success", false, "message", "Email already registered");
        }
        User u = new User();
        u.setUsername(request.getUsername());
        u.setEmail(request.getEmail());
        u.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(u);

        return Map.of("success", true, "message", "Registered successfully");
    }

    // Login -> ALWAYS AuthResponse
    public AuthResponse login(LoginRequest request) {
        var userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return new AuthResponse(false, null, Map.of("message", "Invalid email or password"));
        }
        var user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new AuthResponse(false, null, Map.of("message", "Invalid email or password"));
        }
        String token = UUID.randomUUID().toString();
        return new AuthResponse(true, token, Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "name", user.getUsername()
        ));
    }
}
