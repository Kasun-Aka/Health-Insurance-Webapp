package com.example.healthinsuranceweb.Controller;

import com.example.healthinsuranceweb.Model.Payment;
import com.example.healthinsuranceweb.DTO.PaymentRequest;
import com.example.healthinsuranceweb.Service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class paymentController {

    private final PaymentService paymentService;

    public paymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/new")
    public ResponseEntity<Payment> newPayment(@RequestBody PaymentRequest request) {
        Payment createdPayment = paymentService.processPayment(
                request.getUserId(),
                request.getPolicyId(),
                request.getAmount(),
                request.getMethod()
        );

        return ResponseEntity.ok(createdPayment);
    }

    @GetMapping("/view/{userId}")
    public ResponseEntity<List<Payment>> getUserPayments(@PathVariable Long userId) {
        List<Payment> payments = paymentService.findPaymentsByUserId(userId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/view/all")
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.findAllPayments();
        return ResponseEntity.ok(payments);
    }

}