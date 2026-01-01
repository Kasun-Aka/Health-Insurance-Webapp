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

    private boolean active = true;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    public String getImageUrl() {
        return "/assets/" + imageUrl;
    }

    private String benefits;
}