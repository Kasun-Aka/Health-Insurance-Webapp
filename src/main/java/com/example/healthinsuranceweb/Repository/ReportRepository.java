package com.example.healthinsuranceweb.Repository;

import com.example.healthinsuranceweb.Model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query(value = "SELECT COUNT(*) FROM users", nativeQuery = true)
    Long countUsers();

    @Query(value = "SELECT COUNT(*) FROM user_policies", nativeQuery = true)
    Long countPoliciesChosen();

    @Query(value = "SELECT COUNT(*) FROM claims", nativeQuery = true)
    Long countClaimsMade();

    @Query(value = "SELECT COALESCE(SUM(amount), 0) FROM payments", nativeQuery = true)
    Double sumEarnings();

    @Query(value = "SELECT TOP 1 policy_name FROM user_policies GROUP BY policy_name ORDER BY COUNT(*) DESC", nativeQuery = true)
    Object[] findMostSelectedPolicyName();

    @Query(value = "SELECT ip.policy_name, AVG(r.rating) AS average_rating " +
            "FROM review r " +
            "JOIN policies ip ON r.insurance_package_id = ip.id " +
            "GROUP BY ip.policy_name " +
            "ORDER BY average_rating DESC", nativeQuery = true)
    List<Object[]> findAverageRatingsByPackage();

    @Query(value = "SELECT LEFT(DATENAME(MONTH, payment_date), 3) AS month, " +
            "YEAR(payment_date) AS year, " +
            "SUM(amount) AS total " +
            "FROM payments " +
            "GROUP BY YEAR(payment_date), MONTH(payment_date), DATENAME(MONTH, payment_date) " +
            "ORDER BY year, MONTH(payment_date)",
            nativeQuery = true)
    List<Object[]> findMonthlyEarnings();

}
