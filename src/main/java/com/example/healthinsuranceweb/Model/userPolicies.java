package com.example.healthinsuranceweb.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_policies")
public class userPolicies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userPolicyId;

    @Column(nullable = false)
    private Long userId; // Stores the ID fetched from local storage

    @Column(nullable = false)
    private String userName; // Stores the username fetched from local storage

    @Column(nullable = false)
    private String policyName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal rate;

    private Integer durationDays;

    private boolean active = true;

}