package com.example.healthinsuranceweb.Service;

import com.example.healthinsuranceweb.Model.Payment;
import com.example.healthinsuranceweb.Entity.User;
import com.example.healthinsuranceweb.Model.policy;
import com.example.healthinsuranceweb.Repository.PaymentRepository;
import com.example.healthinsuranceweb.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    //  Use your existing policyService — don’t directly use the repository
    @Autowired
    private policyService policyService;

    /**
     * Process a new payment for a user and policy.
     */
    public Payment processPayment(Long userId, Long policyId, BigDecimal amount, String method) {
        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get policy through the service (not the repository)
        policy selectedPolicy = policyService.getPolicy(policyId);
        if (selectedPolicy == null) {
            throw new RuntimeException("Policy not found");
        }

        // Create payment record
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setPolicy(selectedPolicy);
        payment.setAmount(amount != null ? amount : selectedPolicy.getRate());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentStatus("COMPLETED");
        payment.setPaymentMethod(method);
        payment.setTransactionId("TXN" + System.currentTimeMillis());

        return paymentRepository.save(payment);
    }

    /**
     * Retrieve all payments made by a user.
     */
    public List<Payment> getUserPayments(Long userId) {
        return paymentRepository.findByUserIdOrderByPaymentDateDesc(userId);
    }

    /**
     * Retrieve a policy by ID via the service.
     */
    public policy getPolicyById(Long policyId) {
        return policyService.getPolicy(policyId);
    }
}
