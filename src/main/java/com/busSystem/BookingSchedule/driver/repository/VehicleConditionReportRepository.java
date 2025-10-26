package com.busSystem.BookingSchedule.driver.repository;

import com.busSystem.BookingSchedule.driver.model.VehicleConditionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleConditionReportRepository extends JpaRepository<VehicleConditionReport, Long> {
    List<VehicleConditionReport> findByDriverIdAndStatus(Long driverId, String status);
    
    @Query("SELECT vcr FROM VehicleConditionReport vcr WHERE vcr.driverId = :driverId AND vcr.status = 'ACTIVE' ORDER BY vcr.createdDate DESC")
    List<VehicleConditionReport> findActiveByDriverId(@Param("driverId") Long driverId);
}