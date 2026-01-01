package com.example.healthinsuranceweb.Repository;


import com.example.healthinsuranceweb.Entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    // traverse relation: user.email
    Optional<UserProfile> findByUser_Email(String email);

    boolean existsByNic(String nic);

    Optional<UserProfile> findByUserId(Long userId);
}