package com.busSystem.BookingSchedule.driver.service;

import com.busSystem.BookingSchedule.driver.model.TripLog;
import com.busSystem.BookingSchedule.driver.repository.TripLogRepository;
import com.busSystem.BookingSchedule.user.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class TripLogService extends Service<TripLog, Long> {

    @Autowired
    private TripLogRepository tripLogRepository;

    // Implement abstract methods from base Service class
    @Override
    public List<TripLog> getAll() {
        return tripLogRepository.findAll();
    }

    @Override
    public Optional<TripLog> getById(Long id) {
        return tripLogRepository.findById(id);
    }

    @Override
    public TripLog save(TripLog tripLog) {
        tripLog.setUpdatedDate(LocalDateTime.now());
        if (tripLog.getId() == null) {
            tripLog.setCreatedDate(LocalDateTime.now());
        }
        return tripLogRepository.save(tripLog);
    }

    @Override
    public void delete(Long id) {
        Optional<TripLog> tripLog = tripLogRepository.findById(id);
        if (tripLog.isPresent()) {
            TripLog log = tripLog.get();
            log.setStatus("INACTIVE");
            log.setUpdatedDate(LocalDateTime.now());
            tripLogRepository.save(log);
        }
    }

    @Override
    public boolean exists(Long id) {
        return tripLogRepository.existsById(id);
    }

    // Specific business logic methods only
    public List<TripLog> getAllTripLogsByDriverId(Long driverId) {
        return tripLogRepository.findActiveByDriverId(driverId);
    }
    
    public TripLog updateTripLog(Long id, TripLog updatedTripLog) {
        Optional<TripLog> existingTripLog = tripLogRepository.findById(id);
        if (existingTripLog.isPresent()) {
            TripLog tripLog = existingTripLog.get();
            tripLog.setRouteId(updatedTripLog.getRouteId());
            tripLog.setScheduleId(updatedTripLog.getScheduleId());
            tripLog.setStartTime(updatedTripLog.getStartTime());
            tripLog.setEndTime(updatedTripLog.getEndTime());
            tripLog.setPassengerCount(updatedTripLog.getPassengerCount());
            tripLog.setIssues(updatedTripLog.getIssues());
            tripLog.setUpdatedDate(LocalDateTime.now());
            return tripLogRepository.save(tripLog);
        }
        return null;
    }
}