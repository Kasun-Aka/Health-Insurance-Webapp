package com.example.healthinsuranceweb.Service;

import com.example.healthinsuranceweb.Model.Review;
import com.example.healthinsuranceweb.Model.user;
import com.example.healthinsuranceweb.Model.InsurancePackage;
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

    // Register User
    @Transactional
    public user registerUser(user user) {
        try {
            System.out.println("Checking if username exists: " + user.getUsername());

            TypedQuery<Long> countQuery = entityManager.createQuery(
                    "SELECT COUNT(u) FROM user u WHERE u.username = :username", Long.class);
            countQuery.setParameter("username", user.getUsername());
            Long count = countQuery.getSingleResult();

            System.out.println("   Found " + count + " existing users with username: " + user.getUsername());

            if (count > 0) {
                throw new IllegalArgumentException("Username already exists.");
            }

            System.out.println("Persisting new user to database...");
            entityManager.persist(user);
            entityManager.flush();

            System.out.println("User persisted successfully with ID: " + user.getId());

            return user;
        } catch (Exception e) {
            System.err.println("Error in registerUser: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Login User
    public user loginUser(String username, String password) {
        try {
            System.out.println("Searching for user: " + username);

            TypedQuery<user> query = entityManager.createQuery(
                    "SELECT u FROM user u WHERE u.username = :username AND u.password = :password", user.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            List<user> results = query.getResultList();

            if (results.isEmpty()) {
                System.out.println("No user found with credentials");
                return null;
            } else {
                System.out.println("User found: " + results.get(0).getUsername());
                return results.get(0);
            }
        } catch (Exception e) {
            System.err.println("Error in loginUser: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Add Review by Package Name (creates package if it doesn't exist)
    @Transactional
    public Review addReviewByPackageName(Long userId, String packageName, String comment, int rating) {
        try {
            System.out.println("Finding user with ID: " + userId);
            user user = entityManager.find(com.example.healthinsuranceweb.Model.user.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("User with ID " + userId + " not found.");
            }
            System.out.println("User found: " + user.getUsername());

            // Find or create package
            System.out.println("Searching for package: " + packageName);
            TypedQuery<InsurancePackage> packageQuery = entityManager.createQuery(
                    "SELECT p FROM InsurancePackage p WHERE p.packageName = :packageName", InsurancePackage.class);
            packageQuery.setParameter("packageName", packageName);
            List<InsurancePackage> packages = packageQuery.getResultList();

            InsurancePackage insurancePackage;
            if (packages.isEmpty()) {
                System.out.println("Package not found, creating new package: " + packageName);
                insurancePackage = new InsurancePackage();
                insurancePackage.setPackageName(packageName);
                insurancePackage.setDescription("Auto-created package");
                entityManager.persist(insurancePackage);
                entityManager.flush();
                System.out.println("New package created with ID: " + insurancePackage.getId());
            } else {
                insurancePackage = packages.get(0);
                System.out.println("Package found: " + insurancePackage.getPackageName());
            }

            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5.");
            }

            Review review = new Review();
            review.setUser(user);
            review.setInsurancePackage(insurancePackage);
            review.setComment(comment);
            review.setRating(rating);

            System.out.println("Persisting review...");
            entityManager.persist(review);
            entityManager.flush();
            System.out.println("Review saved with ID: " + review.getId());

            return review;
        } catch (Exception e) {
            System.err.println("Error adding review: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
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

    // Get All Reviews
    public List<Review> getAllReviews() {
        TypedQuery<Review> query = entityManager.createQuery("SELECT r FROM Review r", Review.class);
        return query.getResultList();
    }
}