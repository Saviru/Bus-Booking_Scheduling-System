package com.busSystem.BookingSchedule.driver.service;

import com.busSystem.BookingSchedule.driver.model.TripLog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TripLogService {
    
    List<TripLog> getAllTripLogs();
    
    Optional<TripLog> getTripLogById(Long id);
    
    TripLog saveTripLog(TripLog tripLog);
    
    void deleteTripLog(Long id);
    
    List<TripLog> getTripLogsByDriverId(String driverId);
    
    List<TripLog> getTripLogsByDriverName(String driverName);
    
    List<TripLog> getTripLogsByRouteId(String routeId);
    
    List<TripLog> getTripLogsByVehicleNumber(String vehicleNumber);
    
    List<TripLog> getTripLogsByStatus(String status);
    
    List<TripLog> getTripLogsByDate(LocalDate date);
    
    List<TripLog> getTripLogsByDateRange(LocalDateTime start, LocalDateTime end);
    
    // Performance metrics
    Integer getTotalPassengersCarriedByDriver(String driverId);
    
    Long getCompletedTripsCountByDriver(String driverId);
    
    Double getAveragePassengersForRoute(String routeId);
    
    Long getTotalDrivingTimeMinutesByDriver(String driverId);
    
    Map<String, Object> getDriverPerformanceMetrics(String driverId);
}