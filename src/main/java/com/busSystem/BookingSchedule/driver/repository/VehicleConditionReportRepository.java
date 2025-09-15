package com.busSystem.BookingSchedule.driver.repository;

import com.busSystem.BookingSchedule.driver.model.VehicleConditionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VehicleConditionReportRepository extends JpaRepository<VehicleConditionReport, Long> {
    
    List<VehicleConditionReport> findByVehicleNumber(String vehicleNumber);
    
    List<VehicleConditionReport> findByDriverId(String driverId);
    
    List<VehicleConditionReport> findByDriverName(String driverName);
    
    List<VehicleConditionReport> findByStatus(String status);
    
    List<VehicleConditionReport> findByReportType(String reportType);
    
    List<VehicleConditionReport> findByReportDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT vcr FROM VehicleConditionReport vcr WHERE vcr.brakesCondition = 'NEEDS_ATTENTION' OR " +
           "vcr.lightsCondition = 'NEEDS_ATTENTION' OR vcr.tiresCondition = 'NEEDS_ATTENTION' OR " +
           "vcr.exteriorCondition = 'NEEDS_ATTENTION' OR vcr.interiorCondition = 'NEEDS_ATTENTION'")
    List<VehicleConditionReport> findReportsNeedingAttention();
    
    @Query("SELECT vcr FROM VehicleConditionReport vcr WHERE vcr.vehicleNumber = :vehicleNumber ORDER BY vcr.reportDate DESC")
    List<VehicleConditionReport> findLatestReportsByVehicle(@Param("vehicleNumber") String vehicleNumber);
    
    @Query("SELECT COUNT(vcr) FROM VehicleConditionReport vcr WHERE vcr.vehicleNumber = :vehicleNumber AND " +
           "(vcr.brakesCondition = 'NEEDS_ATTENTION' OR vcr.brakesCondition = 'POOR')")
    Long countBrakeIssuesByVehicle(@Param("vehicleNumber") String vehicleNumber);
}