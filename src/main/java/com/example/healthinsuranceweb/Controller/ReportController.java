package com.example.healthinsuranceweb.Controller;

import com.example.healthinsuranceweb.exception.ReportNotFoundException;
import com.example.healthinsuranceweb.Model.Report;
import com.example.healthinsuranceweb.Service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    // Auto-generated report
    @PostMapping
    public Report createReport() {
        return reportService.generateAndSaveReport();
    }

    // Manual report with user input (No more redundant setters here)
    @PostMapping("/manual")
    public Report createManualReport(@RequestBody Report report) {
        return reportService.saveManualReport(report);
    }

    @GetMapping
    public List<Report> getAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable Long id) {
        return reportService.getReportById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Report> updateReport(@PathVariable Long id, @RequestBody Report updatedReport) {
        return reportService.updateReport(id, updatedReport)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    // ⭐️ Updated return type for clean 200 OK or 404 NOT FOUND
    public ResponseEntity<String> deleteReport(@PathVariable Long id) {
        try {
            reportService.deleteReport(id);
            return ResponseEntity.ok("✅ Report deleted successfully");
        } catch (ReportNotFoundException e) {
            // Handled by the @ResponseStatus(HttpStatus.NOT_FOUND) on the exception
            throw e;
        }
    }

    // 💰 Get stats endpoints (for the dashboard)
    @GetMapping("/users")
    public Integer getTotalUsers() {
        return reportService.getTotalCustomers();
    }

    @GetMapping("/policies")
    public Integer getTotalPoliciesChosen() {
        return reportService.getTotalPoliciesChosen();
    }

    @GetMapping("/claims")
    public Integer getTotalClaims() {
        return reportService.getTotalClaims();
    }

    @GetMapping("/earnings")
    public Double getTotalEarnings() {
        return reportService.getTotalEarnings();
    }

    @GetMapping("/top-policy")
    public Long getMostSelectedPolicy() {
        return reportService.getMostSelectedPolicyId();
    }
}