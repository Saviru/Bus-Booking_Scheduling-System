package com.busSystem.BookingSchedule.driver.service;

import com.busSystem.BookingSchedule.driver.model.VehicleConditionReport;
import com.busSystem.BookingSchedule.driver.repository.VehicleConditionReportRepository;
import com.busSystem.BookingSchedule.user.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class VehicleConditionReportService extends Service<VehicleConditionReport, Long> {

    @Autowired
    private VehicleConditionReportRepository vehicleConditionReportRepository;

    // Implement abstract methods from base Service class
    @Override
    public List<VehicleConditionReport> getAll() {
        return vehicleConditionReportRepository.findAll();
    }

    @Override
    public Optional<VehicleConditionReport> getById(Long id) {
        return vehicleConditionReportRepository.findById(id);
    }

    @Override
    public VehicleConditionReport save(VehicleConditionReport report) {
        report.setUpdatedDate(LocalDateTime.now());
        if (report.getId() == null) {
            report.setCreatedDate(LocalDateTime.now());
        }
        return vehicleConditionReportRepository.save(report);
    }

    @Override
    public void delete(Long id) {
        Optional<VehicleConditionReport> report = vehicleConditionReportRepository.findById(id);
        if (report.isPresent()) {
            VehicleConditionReport r = report.get();
            r.setStatus("INACTIVE");
            r.setUpdatedDate(LocalDateTime.now());
            vehicleConditionReportRepository.save(r);
        }
    }

    @Override
    public boolean exists(Long id) {
        return vehicleConditionReportRepository.existsById(id);
    }

    // Specific business logic methods only
    public List<VehicleConditionReport> getAllReportsByDriverId(Long driverId) {
        return vehicleConditionReportRepository.findActiveByDriverId(driverId);
    }
    
    public VehicleConditionReport updateReport(Long id, VehicleConditionReport updatedReport) {
        Optional<VehicleConditionReport> existingReport = vehicleConditionReportRepository.findById(id);
        if (existingReport.isPresent()) {
            VehicleConditionReport report = existingReport.get();
            report.setVehicleNumber(updatedReport.getVehicleNumber());
            report.setBrakesCondition(updatedReport.getBrakesCondition());
            report.setLightsCondition(updatedReport.getLightsCondition());
            report.setTiresCondition(updatedReport.getTiresCondition());
            report.setEngineCondition(updatedReport.getEngineCondition());
            report.setOtherIssues(updatedReport.getOtherIssues());
            report.setOverallStatus(updatedReport.getOverallStatus());
            report.setFollowUpNotes(updatedReport.getFollowUpNotes());
            report.setUpdatedDate(LocalDateTime.now());
            return vehicleConditionReportRepository.save(report);
        }
        return null;
    }
}