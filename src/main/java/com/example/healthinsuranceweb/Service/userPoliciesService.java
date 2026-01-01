package com.example.healthinsuranceweb.Service;

import com.example.healthinsuranceweb.Model.userPolicies;
import com.example.healthinsuranceweb.Repository.userPoliciesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class userPoliciesService {

    private final userPoliciesRepository userPoliciesRepository;

    public userPoliciesService(com.example.healthinsuranceweb.Repository.userPoliciesRepository userPoliciesRepository) {
        this.userPoliciesRepository = userPoliciesRepository;
    }

    /**
     * Saves a new user policy record to the database, first deactivating any existing active policies.
     * @param newPolicy The userPolicies object to be saved.
     * @return The saved userPolicies object.
     */
    @Transactional // Ensures both operations (deactivate and save) succeed or fail together
    public userPolicies addUserPolicy(userPolicies newPolicy) {
        // 1. Deactivate any currently active policies for this user
        userPoliciesRepository.deactivateAllActivePolicies(newPolicy.getUserId());

        // 2. Ensure the new policy is marked as active
        newPolicy.setActive(true);

        // 3. Save the new policy
        return userPoliciesRepository.save(newPolicy);
    }

    public Optional<userPolicies> getActiveUserPolicy(Long userId) {
        // This method will now reliably return 0 or 1 result
        return userPoliciesRepository.findByUserIdAndActiveTrue(userId);
    }
}