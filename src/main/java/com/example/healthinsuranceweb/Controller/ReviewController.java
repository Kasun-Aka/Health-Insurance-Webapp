package com.example.healthinsuranceweb.Controller;

import com.example.healthinsuranceweb.DTO.ReviewRequest;
import com.example.healthinsuranceweb.Model.Review;
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


    // 🔹 Add Review (by package name - creates package if doesn't exist)
    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestBody ReviewRequest req) {
        Review review = reviewService.addReviewByPackageName(
                req.getUserId(), req.getPackageName(), req.getComment(), req.getRating()
        );
        return ResponseEntity.ok(review);
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

    @GetMapping("/api/auth/me")
    public ResponseEntity<Long> getCurrentUserId() {
        // If you are using Spring Security:
        // Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Return the ID from that principal.

        // For now, if you are just testing, you can return a hardcoded ID
        // to ensure the frontend logic works:
        return ResponseEntity.ok(1L);
    }
}