package com.example.healthinsuranceweb.Controller;

import com.example.healthinsuranceweb.Model.policy;
import com.example.healthinsuranceweb.Model.userPolicies;
import com.example.healthinsuranceweb.Service.policyService;
import com.example.healthinsuranceweb.Service.userPoliciesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/policies")
public class policyController {

    private final policyService policyService;
    private final userPoliciesService userPoliciesService;

    // UPDATED CONSTRUCTOR TO INJECT BOTH SERVICES
    public policyController(policyService policyService, userPoliciesService userPoliciesService) {
        this.policyService = policyService;
        this.userPoliciesService = userPoliciesService;
    }

    // ---------------- CUSTOMER ----------------

    // Endpoint to get active policies for general browsing
    @GetMapping("/active")
    public List<policy> getActivePolicies() {
        return policyService.getActivePolicies();
    }

    // Endpoint to register a policy for a user
    @PostMapping("/user")
    public userPolicies addUserPolicy(@RequestBody userPolicies userPolicies) {
        return userPoliciesService.addUserPolicy(userPolicies);
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<userPolicies> getActiveUserPolicy(@PathVariable Long userId) {
        Optional<userPolicies> policy = userPoliciesService.getActiveUserPolicy(userId);

        // Return 200 OK with the policy data, or 404 Not Found if the user has no active policy
        return policy.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ---------------- ADMIN ----------------
    @GetMapping
    public List<policy> getAllPolicies() {
        return policyService.getAllPolicies();
    }

    @GetMapping("/{id}")
    public policy getPolicy(@PathVariable Long id) {
        return policyService.getPolicy(id);
    }

    @PostMapping
    public policy addPolicy(@RequestBody policy policy) {
        return policyService.addPolicy(policy);
    }

    @PutMapping("/{id}")
    public policy updatePolicy(@PathVariable Long id, @RequestBody policy policy) {
        return policyService.updatePolicy(id, policy);
    }

    @DeleteMapping("/{id}")
    public void deletePolicy(@PathVariable Long id) {
        policyService.deletePolicy(id);
    }
}