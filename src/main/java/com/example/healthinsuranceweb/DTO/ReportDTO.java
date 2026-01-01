package com.example.healthinsuranceweb.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private Long id;
    private String reportType;
    private Double totalEarnings;
    private Integer totalCustomers;
    private Integer totalPolicies;
    private Integer totalClaims;
    private LocalDate generatedDate;
    private String createdBy;
}
