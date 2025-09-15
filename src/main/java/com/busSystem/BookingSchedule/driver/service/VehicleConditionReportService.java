package com.busSystem.BookingSchedule.driver.service;

import com.busSystem.BookingSchedule.driver.model.VehicleConditionReport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface VehicleConditionReportService {
    
    List<VehicleConditionReport> getAllReports();
    
    Optional<VehicleConditionReport> getReportById(Long id);
    
    VehicleConditionReport saveReport(VehicleConditionReport report);
    
    void deleteReport(Long id);
    
    List<VehicleConditionReport> getReportsByVehicleNumber(String vehicleNumber);
    
    List<VehicleConditionReport> getReportsByDriverId(String driverId);
    
    List<VehicleConditionReport> getReportsByDriverName(String driverName);
    
    List<VehicleConditionReport> getReportsByStatus(String status);
    
    List<VehicleConditionReport> getReportsByType(String reportType);
    
    List<VehicleConditionReport> getReportsByDate(LocalDate date);
    
    List<VehicleConditionReport> getReportsByDateRange(LocalDateTime start, LocalDateTime end);
    
    List<VehicleConditionReport> getReportsNeedingAttention();
    
    List<VehicleConditionReport> getLatestReportsByVehicle(String vehicleNumber);
    
    Long countBrakeIssuesByVehicle(String vehicleNumber);
    
    VehicleConditionReport updateReportStatus(Long id, String status, String reviewedBy, String maintenanceNotes, String followUpAction);
    
    Map<String, Long> getVehicleIssuesSummary(String vehicleNumber);
}