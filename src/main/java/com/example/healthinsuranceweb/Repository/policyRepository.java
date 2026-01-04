package com.example.healthinsuranceweb.Repository;

import com.example.healthinsuranceweb.Model.policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface policyRepository extends JpaRepository<policy, Long> {
    List<policy> findByActiveTrue();
}
