package com.example.healthinsuranceweb.Service;

import com.example.healthinsuranceweb.Entity.User;
import com.example.healthinsuranceweb.Model.Payment;
import com.example.healthinsuranceweb.Model.policy;
import com.example.healthinsuranceweb.Repository.PaymentRepository;
import com.example.healthinsuranceweb.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private policyService policyService;

    public Payment processPayment(Long userId, Long policyId, BigDecimal amount, String method) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

        policy selectedPolicy = policyService.getPolicy(policyId);
        if (selectedPolicy == null) {
            throw new RuntimeException("Policy with ID " + policyId + " not found");
        }

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setPolicy(selectedPolicy);

        payment.setAmount(amount != null ? amount : selectedPolicy.getRate());

        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentStatus("COMPLETED");
        payment.setPaymentMethod(method);
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8));

        return paymentRepository.save(payment);
    }

    public List<Payment> findPaymentsByUserId(Long userId) {
        // 1. Verify the user exists first (Optional but good for error handling)
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User with ID " + userId + " not found");
        }

        // 2. Fetch the list of payments from the repository
        return paymentRepository.findByUserIdOrderByPaymentDateDesc(userId);
    }

    public List<Payment> findAllPayments() {
        return paymentRepository.findAll();
    }
}