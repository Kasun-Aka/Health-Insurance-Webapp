package com.example.healthinsuranceweb.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "report")

public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reportName;
    private String reportType;
    private String description;

    private Double earnings;
    private Integer totalCustomers;
    private Integer totalPolicies;
    private Integer totalClaims;

    private String generatedDate;
    private String createdBy;
}
