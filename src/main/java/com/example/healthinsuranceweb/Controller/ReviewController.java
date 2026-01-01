package com.example.healthinsuranceweb.Controller;

import com.example.healthinsuranceweb.Model.Review;
import com.example.healthinsuranceweb.Model.user;
import com.example.healthinsuranceweb.Service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // 🔹 Register User
    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestBody user user) {
        try {
            System.out.println("📥 Registration request received:");
            System.out.println("   Name: " + user.getName());
            System.out.println("   Username: " + user.getUsername());
            System.out.println("   Email: " + user.getEmail());

            user registeredUser = reviewService.registerUser(user);

            System.out.println("✅ User registered successfully with ID: " + registeredUser.getId());

            return ResponseEntity.ok(registeredUser);
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Registration failed - Validation error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Registration failed - Server error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed: " + e.getMessage());
        }
    }

    // 🔹 Login User
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String username,
                                       @RequestParam String password) {
        try {
            System.out.println("🔐 Login attempt for username: " + username);

            user loggedInUser = reviewService.loginUser(username, password);

            if (loggedInUser != null) {
                System.out.println("✅ Login successful for user: " + username);
                return ResponseEntity.ok(loggedInUser);
            } else {
                System.out.println("❌ Login failed - Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid username or password");
            }
        } catch (Exception e) {
            System.err.println("❌ Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Login failed: " + e.getMessage());
        }
    }

    // 🔹 Add Review (by package name - creates package if doesn't exist)
    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestParam Long userId,
                                       @RequestParam String packageName,
                                       @RequestParam String comment,
                                       @RequestParam int rating) {
        try {
            System.out.println("📝 Adding review - User: " + userId + ", Package: " + packageName);

            Review review = reviewService.addReviewByPackageName(userId, packageName, comment, rating);

            System.out.println("✅ Review added with ID: " + review.getId());

            return ResponseEntity.ok(review);
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Review validation failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Review creation failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add review: " + e.getMessage());
        }
    }

    // 🔹 Update Review
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id,
                                          @RequestParam String comment,
                                          @RequestParam int rating) {
        try {
            Review updated = reviewService.updateReview(id, comment, rating);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Update failed: " + e.getMessage());
        }
    }

    // 🔹 Delete Review
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        try {
            String result = reviewService.deleteReview(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Delete failed: " + e.getMessage());
        }
    }

    // 🔹 Get Reviews by Package Name
    @GetMapping("/package/name/{packageName}")
    public ResponseEntity<?> getReviewsByPackageName(@PathVariable String packageName) {
        try {
            List<Review> reviews = reviewService.getReviewsByPackageName(packageName);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch reviews: " + e.getMessage());
        }
    }

    // 🔹 Get All Reviews
    @GetMapping("/all")
    public ResponseEntity<?> getAllReviews() {
        try {
            List<Review> reviews = reviewService.getAllReviews();
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch reviews: " + e.getMessage());
        }
    }
}