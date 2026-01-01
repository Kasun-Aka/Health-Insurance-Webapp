package com.example.healthinsuranceweb.Repository;

import com.example.healthinsuranceweb.Model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query(value = "SELECT COUNT(*) FROM users", nativeQuery = true)
    Long countUsers();

    @Query(value = "SELECT COUNT(*) FROM user_policies", nativeQuery = true)
    Long countPoliciesChosen();

    @Query(value = "SELECT COUNT(*) FROM claims", nativeQuery = true)
    Long countClaimsMade();

    @Query(value = "SELECT SUM(premium) FROM user_policies", nativeQuery = true)
    Double sumEarnings();

    @Query(value = "SELECT TOP 1 policy_id FROM user_policies GROUP BY policy_id ORDER BY COUNT(*) DESC", nativeQuery = true)
    Long findMostSelectedPolicyId();
}
