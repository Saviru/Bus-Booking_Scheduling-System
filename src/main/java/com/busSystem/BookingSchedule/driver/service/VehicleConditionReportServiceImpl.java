package com.busSystem.BookingSchedule.driver.service;

import com.busSystem.BookingSchedule.driver.model.VehicleConditionReport;
import com.busSystem.BookingSchedule.driver.repository.VehicleConditionReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VehicleConditionReportServiceImpl implements VehicleConditionReportService {

    private final VehicleConditionReportRepository reportRepository;
    
    @Autowired
    public VehicleConditionReportServiceImpl(VehicleConditionReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public List<VehicleConditionReport> getAllReports() {
        return reportRepository.findAll();
    }

    @Override
    public Optional<VehicleConditionReport> getReportById(Long id) {
        return reportRepository.findById(id);
    }

    @Override
    @Transactional
    public VehicleConditionReport saveReport(VehicleConditionReport report) {
        return reportRepository.save(report);
    }

    @Override
    @Transactional
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }

    @Override
    public List<VehicleConditionReport> getReportsByVehicleNumber(String vehicleNumber) {
        return reportRepository.findByVehicleNumber(vehicleNumber);
    }

    @Override
    public List<VehicleConditionReport> getReportsByDriverId(String driverId) {
        return reportRepository.findByDriverId(driverId);
    }

    @Override
    public List<VehicleConditionReport> getReportsByDriverName(String driverName) {
        return reportRepository.findByDriverName(driverName);
    }

    @Override
    public List<VehicleConditionReport> getReportsByStatus(String status) {
        return reportRepository.findByStatus(status);
    }

    @Override
    public List<VehicleConditionReport> getReportsByType(String reportType) {
        return reportRepository.findByReportType(reportType);
    }

    @Override
    public List<VehicleConditionReport> getReportsByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return reportRepository.findByReportDateBetween(startOfDay, endOfDay);
    }

    @Override
    public List<VehicleConditionReport> getReportsByDateRange(LocalDateTime start, LocalDateTime end) {
        return reportRepository.findByReportDateBetween(start, end);
    }

    @Override
    public List<VehicleConditionReport> getReportsNeedingAttention() {
        return reportRepository.findReportsNeedingAttention();
    }

    @Override
    public List<VehicleConditionReport> getLatestReportsByVehicle(String vehicleNumber) {
        return reportRepository.findLatestReportsByVehicle(vehicleNumber);
    }

    @Override
    public Long countBrakeIssuesByVehicle(String vehicleNumber) {
        return reportRepository.countBrakeIssuesByVehicle(vehicleNumber);
    }

    @Override
    @Transactional
    public VehicleConditionReport updateReportStatus(Long id, String status, String reviewedBy, 
                                                   String maintenanceNotes, String followUpAction) {
        VehicleConditionReport report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid report ID: " + id));
        
        report.setStatus(status);
        
        if (reviewedBy != null && !reviewedBy.isEmpty()) {
            report.setReviewedBy(reviewedBy);
            report.setReviewedDate(LocalDateTime.now());
        }
        
        if (maintenanceNotes != null && !maintenanceNotes.isEmpty()) {
            report.setMaintenanceNotes(maintenanceNotes);
        }
        
        if (followUpAction != null && !followUpAction.isEmpty()) {
            report.setFollowUpAction(followUpAction);
        }
        
        return reportRepository.save(report);
    }

    @Override
    public Map<String, Long> getVehicleIssuesSummary(String vehicleNumber) {
        List<VehicleConditionReport> reports = reportRepository.findByVehicleNumber(vehicleNumber);
        Map<String, Long> summary = new HashMap<>();
        
        // Count reports by status
        Map<String, Long> statusCounts = reports.stream()
                .collect(Collectors.groupingBy(VehicleConditionReport::getStatus, Collectors.counting()));
        summary.putAll(statusCounts);
        
        // Count reports with specific issues
        long brakeIssues = reports.stream()
                .filter(r -> "POOR".equals(r.getBrakesCondition()) || "NEEDS_ATTENTION".equals(r.getBrakesCondition()))
                .count();
        summary.put("brakeIssues", brakeIssues);
        
        long lightIssues = reports.stream()
                .filter(r -> "POOR".equals(r.getLightsCondition()) || "NEEDS_ATTENTION".equals(r.getLightsCondition()))
                .count();
        summary.put("lightIssues", lightIssues);
        
        long tireIssues = reports.stream()
                .filter(r -> "POOR".equals(r.getTiresCondition()) || "NEEDS_ATTENTION".equals(r.getTiresCondition()))
                .count();
        summary.put("tireIssues", tireIssues);
        
        // Count reports that need attention (any component)
        long needsAttention = reports.stream()
                .filter(VehicleConditionReport::hasIssues)
                .count();
        summary.put("needsAttention", needsAttention);
        
        // Total reports for this vehicle
        summary.put("totalReports", (long) reports.size());
        
        return summary;
    }
}