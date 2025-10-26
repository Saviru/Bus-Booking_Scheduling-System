package com.busSystem.BookingSchedule.driver.repository;

import com.busSystem.BookingSchedule.driver.model.TripLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripLogRepository extends JpaRepository<TripLog, Long> {
    List<TripLog> findByDriverIdAndStatus(Long driverId, String status);
    
    @Query("SELECT tl FROM TripLog tl WHERE tl.driverId = :driverId AND tl.status = 'ACTIVE' ORDER BY tl.createdDate DESC")
    List<TripLog> findActiveByDriverId(@Param("driverId") Long driverId);
}