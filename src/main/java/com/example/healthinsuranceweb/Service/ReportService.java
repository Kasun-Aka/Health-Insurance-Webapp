package com.example.healthinsuranceweb.Service;

import com.example.healthinsuranceweb.Model.Report;
import com.example.healthinsuranceweb.Repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    //  Get total customers
    public Integer getTotalCustomers() {
        try {
            Long count = reportRepository.countUsers();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    //  Get total policies chosen
    public Integer getTotalPoliciesChosen() {
        try {
            Long count = reportRepository.countPoliciesChosen();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    //  Get total claims made
    public Integer getTotalClaims() {
        try {
            Long count = reportRepository.countClaimsMade();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    //  Get total earnings
    public Double getTotalEarnings() {
        try {
            Double earnings = reportRepository.sumEarnings();
            return earnings != null ? earnings : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    //  Get most selected policy
    public Object[] getMostSelectedPolicyName() {
        try {
            return reportRepository.findMostSelectedPolicyName();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Object[]> findAverageRatingsByPackage() {
        try {
            return reportRepository.findAverageRatingsByPackage();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Object[]> findMonthlyEarnings() {
        try {
            return reportRepository.findMonthlyEarnings();
        } catch (Exception e) {
            return null;
        }
    }

    //  Auto-generate report (no user input)
    public Report generateAndSaveReport() {
        Report report = Report.builder()
                .reportName("Monthly Summary - " + LocalDate.now())
                .reportType("System Generated")
                .description("Auto-generated report with live data from database.")
                .earnings(getTotalEarnings())
                .totalCustomers(getTotalCustomers())
                .totalPolicies(getTotalPoliciesChosen())
                .totalClaims(getTotalClaims())
                .build();

        return reportRepository.save(report);
    }

    //  Manually create report (user input + DB data)
    public Report saveManualReport(Report report) {
        try {
            // Validate required fields from the form
            if (report.getReportName() == null || report.getReportName().trim().isEmpty()) {
                throw new IllegalArgumentException("Report name is required");
            }
            if (report.getReportType() == null || report.getReportType().trim().isEmpty()) {
                throw new IllegalArgumentException("Report type is required");
            }

            // Set description to empty string if null
            if (report.getDescription() == null) {
                report.setDescription("");
            }

            // Fetch and set live stats from database
            report.setEarnings(getTotalEarnings());
            report.setTotalCustomers(getTotalCustomers());
            report.setTotalPolicies(getTotalPoliciesChosen());
            report.setTotalClaims(getTotalClaims());

            return reportRepository.save(report);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save report: " + e.getMessage(), e);
        }
    }

    //  Get all reports
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    //  Get single report by ID
    public Optional<Report> getReportById(Long id) {
        return reportRepository.findById(id);
    }

    //  Update existing report (auto-refresh stats)
    public Optional<Report> updateReport(Long id, Report updatedReport) {
        return reportRepository.findById(id).map(report -> {
            // Validate required fields
            if (updatedReport.getReportName() != null && !updatedReport.getReportName().trim().isEmpty()) {
                report.setReportName(updatedReport.getReportName());
            }
            if (updatedReport.getReportType() != null && !updatedReport.getReportType().trim().isEmpty()) {
                report.setReportType(updatedReport.getReportType());
            }
            if (updatedReport.getDescription() != null) {
                report.setDescription(updatedReport.getDescription());
            }

            // Auto-fetch live DB data for these
            report.setEarnings(getTotalEarnings());
            report.setTotalCustomers(getTotalCustomers());
            report.setTotalPolicies(getTotalPoliciesChosen());
            report.setTotalClaims(getTotalClaims());

            return reportRepository.save(report);
        });
    }

    //  Delete a report
    public void deleteReport(Long id) {
        if (!reportRepository.existsById(id)) {
            throw new RuntimeException("Report not found with id: " + id);
        }
        reportRepository.deleteById(id);
    }
}