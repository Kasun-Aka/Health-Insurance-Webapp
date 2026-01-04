package com.example.healthinsuranceweb.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "policies")
public class policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String policyName;

    @Column(columnDefinition = "TEXT") // allows longer description
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal rate;

    private Integer durationDays;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    private String benefits;

    private boolean active = true;

    @JsonIgnore
    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL)
    private List<Payment> payment;
}