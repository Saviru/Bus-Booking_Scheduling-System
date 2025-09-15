package com.busSystem.BookingSchedule.driver.service;

import com.busSystem.BookingSchedule.driver.model.TripLog;
import com.busSystem.BookingSchedule.driver.repository.TripLogRepository;
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

@Service
public class TripLogServiceImpl implements TripLogService {

    private final TripLogRepository tripLogRepository;
    
    @Autowired
    public TripLogServiceImpl(TripLogRepository tripLogRepository) {
        this.tripLogRepository = tripLogRepository;
    }

    @Override
    public List<TripLog> getAllTripLogs() {
        return tripLogRepository.findAll();
    }

    @Override
    public Optional<TripLog> getTripLogById(Long id) {
        return tripLogRepository.findById(id);
    }

    @Override
    @Transactional
    public TripLog saveTripLog(TripLog tripLog) {
        return tripLogRepository.save(tripLog);
    }

    @Override
    @Transactional
    public void deleteTripLog(Long id) {
        tripLogRepository.deleteById(id);
    }

    @Override
    public List<TripLog> getTripLogsByDriverId(String driverId) {
        return tripLogRepository.findByDriverId(driverId);
    }

    @Override
    public List<TripLog> getTripLogsByDriverName(String driverName) {
        return tripLogRepository.findByDriverName(driverName);
    }

    @Override
    public List<TripLog> getTripLogsByRouteId(String routeId) {
        return tripLogRepository.findByRouteId(routeId);
    }

    @Override
    public List<TripLog> getTripLogsByVehicleNumber(String vehicleNumber) {
        return tripLogRepository.findByVehicleNumber(vehicleNumber);
    }

    @Override
    public List<TripLog> getTripLogsByStatus(String status) {
        return tripLogRepository.findByStatus(status);
    }

    @Override
    public List<TripLog> getTripLogsByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return tripLogRepository.findByDate(startOfDay, endOfDay);
    }

    @Override
    public List<TripLog> getTripLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return tripLogRepository.findByStartTimeBetween(start, end);
    }

    @Override
    public Integer getTotalPassengersCarriedByDriver(String driverId) {
        Integer total = tripLogRepository.getTotalPassengersCarriedByDriver(driverId);
        return total != null ? total : 0;
    }

    @Override
    public Long getCompletedTripsCountByDriver(String driverId) {
        Long count = tripLogRepository.getCompletedTripsCountByDriver(driverId);
        return count != null ? count : 0L;
    }

    @Override
    public Double getAveragePassengersForRoute(String routeId) {
        Double average = tripLogRepository.getAveragePassengersForRoute(routeId);
        return average != null ? average : 0.0;
    }

    @Override
    public Long getTotalDrivingTimeMinutesByDriver(String driverId) {
        Long total = tripLogRepository.getTotalDrivingTimeMinutesByDriver(driverId);
        return total != null ? total : 0L;
    }

    @Override
    public Map<String, Object> getDriverPerformanceMetrics(String driverId) {
        Map<String, Object> metrics = new HashMap<>();
        
        // Get all trip logs for this driver
        List<TripLog> driverTrips = getTripLogsByDriverId(driverId);
        
        // Total trips
        metrics.put("totalTrips", driverTrips.size());
        
        // Completed trips
        Long completedTrips = getCompletedTripsCountByDriver(driverId);
        metrics.put("completedTrips", completedTrips);
        
        // Total passengers
        Integer totalPassengers = getTotalPassengersCarriedByDriver(driverId);
        metrics.put("totalPassengers", totalPassengers);
        
        // Average passengers per trip
        Double avgPassengers = completedTrips > 0 ? totalPassengers.doubleValue() / completedTrips : 0.0;
        metrics.put("averagePassengersPerTrip", avgPassengers);
        
        // Total driving time (minutes)
        Long totalDrivingMinutes = getTotalDrivingTimeMinutesByDriver(driverId);
        metrics.put("totalDrivingMinutes", totalDrivingMinutes);
        
        // Average trip duration
        Double avgTripDuration = completedTrips > 0 ? totalDrivingMinutes.doubleValue() / completedTrips : 0.0;
        metrics.put("averageTripDurationMinutes", avgTripDuration);
        
        return metrics;
    }
}