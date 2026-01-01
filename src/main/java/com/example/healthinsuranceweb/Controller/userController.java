package com.example.healthinsuranceweb.Controller;


import com.example.healthinsuranceweb.Entity.User;
import com.example.healthinsuranceweb.Repository.UserProfileRepository;
import com.example.healthinsuranceweb.Repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class userController {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public userController(UserRepository userRepository, UserProfileRepository userProfileRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
    }

    // Get all users for system report
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            System.err.println("Error fetching all users: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            return userRepository.findById(id)
                    .map(user -> ResponseEntity.ok(user))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Error fetching user by ID: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get user by email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        try {
            return userRepository.findByEmail(email)
                    .map(user -> ResponseEntity.ok(user))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Error fetching user by email: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get user count for dashboard
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getUserCount() {
        try {
            long count = userRepository.count();
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error getting user count: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // Delete user by ID
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        try {
            Map<String, Object> response = new HashMap<>();

            // Check if user exists
            if (!userRepository.existsById(id)) {
                response.put("success", false);
                response.put("message", "User not found with ID: " + id);
                return ResponseEntity.notFound().build();
            }

            // Delete user profile first (if it exists)
            userProfileRepository.findByUserId(id).ifPresent(profile -> {
                userProfileRepository.delete(profile);
            });

            // Delete user
            userRepository.deleteById(id);

            response.put("success", true);
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to delete user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("User service is running");
    }
}
