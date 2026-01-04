package com.example.healthinsuranceweb.DTO;

import lombok.Data;
import java.math.BigDecimal;

@Data // Lombok generates getters, setters, and constructors
public class PaymentRequest {
    private Long userId;
    private Long policyId;
    private BigDecimal amount;
    private String method;
}