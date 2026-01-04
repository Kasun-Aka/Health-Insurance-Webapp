package com.example.healthinsuranceweb.Service;

import com.example.healthinsuranceweb.Entity.User;
import com.example.healthinsuranceweb.Model.Review;
import com.example.healthinsuranceweb.Model.policy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Review addReviewByPackageName(Long userId, String packageName, String comment, int rating) {
        // 1. Find User
        User user = entityManager.find(User.class, userId);
        if (user == null) throw new IllegalArgumentException("User not found.");

        // 2. Find Policy (Use the Entity name 'policy' and field 'policyName')
        TypedQuery<policy> query = entityManager.createQuery(
                "SELECT p FROM policy p WHERE p.policyName = :name", policy.class);
        query.setParameter("name", packageName);

        List<policy> results = query.getResultList();
        if (results.isEmpty()) {
            throw new IllegalArgumentException("Insurance package '" + packageName + "' does not exist.");
        }
        policy insurancePackage = results.get(0);

        // 3. Validation
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be 1-5.");

        // 4. Save Review
        Review review = new Review();
        review.setUser(user);
        review.setInsurancePackage(insurancePackage);
        review.setComment(comment);
        review.setRating(rating);

        entityManager.persist(review);
        return review;
    }

    public List<Review> getAllReviews() {
        return entityManager.createQuery("SELECT r FROM Review r", Review.class).getResultList();
    }

    // Update Review
    @Transactional
    public Review updateReview(Long id, String comment, int rating) {
        Review review = entityManager.find(Review.class, id);
        if (review == null) {
            throw new IllegalArgumentException("Review not found.");
        }
        review.setComment(comment);
        review.setRating(rating);
        entityManager.merge(review);
        return review;
    }

    // Delete Review
    @Transactional
    public String deleteReview(Long id) {
        Review review = entityManager.find(Review.class, id);
        if (review != null) {
            entityManager.remove(review);
            return "Review deleted successfully.";
        }
        return "Review not found.";
    }

    // Get Reviews by Package Name
    public List<Review> getReviewsByPackageName(String packageName) {
        TypedQuery<Review> query = entityManager.createQuery(
                "SELECT r FROM Review r WHERE r.insurancePackage.packageName = :packageName", Review.class);
        query.setParameter("packageName", packageName);
        return query.getResultList();
    }
}