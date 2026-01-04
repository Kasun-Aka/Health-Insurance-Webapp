package com.example.healthinsuranceweb.Service;

import com.example.healthinsuranceweb.Model.policy;
import com.example.healthinsuranceweb.Repository.policyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class policyService {

    private final policyRepository policyRepository;

    public policyService(policyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    // Admin - add new policy
    public policy addPolicy(policy policy) {
        return policyRepository.save(policy);
    }

    // Admin - update policy
    public policy updatePolicy(Long id, policy updatedPolicy) {
        return policyRepository.findById(id)
                .map(policy -> {
                    policy.setPolicyName(updatedPolicy.getPolicyName());
                    policy.setDescription(updatedPolicy.getDescription());
                    policy.setRate(updatedPolicy.getRate());
                    policy.setDurationDays(updatedPolicy.getDurationDays());
                    policy.setBenefits(updatedPolicy.getBenefits());
                    policy.setImageUrl(updatedPolicy.getImageUrl());
                    policy.setActive(updatedPolicy.isActive());
                    return policyRepository.save(policy);
                })
                .orElse(null); // could also throw exception
    }

    // Admin - delete policy
    public void deletePolicy(Long id) {
        policyRepository.deleteById(id);
    }

    // Customer - view active policies
    public List<policy> getActivePolicies() {
        return policyRepository.findByActiveTrue();
    }

    // Admin - view all policies
    public List<policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    // Get single policy
    public policy getPolicy(Long id) {
        return policyRepository.findById(id).orElse(null);
    }
}
