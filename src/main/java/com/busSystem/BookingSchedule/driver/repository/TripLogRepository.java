package com.busSystem.BookingSchedule.driver.repository;

import com.busSystem.BookingSchedule.driver.model.TripLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripLogRepository extends JpaRepository<TripLog, Long> {
    
    List<TripLog> findByDriverId(String driverId);
    
    List<TripLog> findByDriverName(String driverName);
    
    List<TripLog> findByRouteId(String routeId);
    
    List<TripLog> findByVehicleNumber(String vehicleNumber);
    
    List<TripLog> findByStatus(String status);
    
    List<TripLog> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT t FROM TripLog t WHERE t.startTime >= :date AND t.endTime <= :nextDate")
    List<TripLog> findByDate(@Param("date") LocalDateTime date, @Param("nextDate") LocalDateTime nextDate);
    
    @Query("SELECT SUM(t.passengerCount) FROM TripLog t WHERE t.driverId = :driverId AND t.status = 'COMPLETED'")
    Integer getTotalPassengersCarriedByDriver(@Param("driverId") String driverId);
    
    @Query("SELECT COUNT(t) FROM TripLog t WHERE t.driverId = :driverId AND t.status = 'COMPLETED'")
    Long getCompletedTripsCountByDriver(@Param("driverId") String driverId);
    
    @Query("SELECT AVG(t.passengerCount) FROM TripLog t WHERE t.routeId = :routeId AND t.status = 'COMPLETED'")
    Double getAveragePassengersForRoute(@Param("routeId") String routeId);
    
    @Query("SELECT SUM(FUNCTION('TIMESTAMPDIFF', MINUTE, t.startTime, t.endTime)) FROM TripLog t " +
           "WHERE t.driverId = :driverId AND t.status = 'COMPLETED'")
    Long getTotalDrivingTimeMinutesByDriver(@Param("driverId") String driverId);
}