package com.example.healthinsuranceweb.Repository;

import com.example.healthinsuranceweb.Model.userPolicies;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface userPoliciesRepository extends JpaRepository<userPolicies, Long> {
    List<userPolicies> findByActiveTrue();

    // The original method now works reliably because the service layer ensures only one is active.
    Optional<userPolicies> findByUserIdAndActiveTrue(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE userPolicies p SET p.active = false WHERE p.userId = :userId AND p.active = true")
    void deactivateAllActivePolicies(@Param("userId") Long userId);
}