package com.busSystem.BookingSchedule.operationsManager.service;

import com.busSystem.BookingSchedule.operationsManager.model.Schedule;
import com.busSystem.BookingSchedule.operationsManager.repository.ScheduleRepository;
import com.busSystem.BookingSchedule.user.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ScheduleService extends Service<Schedule, Long> {

    @Autowired
    private ScheduleRepository scheduleRepository;

    // Implement abstract methods from base Service class
    @Override
    public List<Schedule> getAll() {
        return scheduleRepository.findAll();
    }

    @Override
    public Optional<Schedule> getById(Long id) {
        return scheduleRepository.findById(id);
    }

    @Override
    public Schedule save(Schedule schedule) {
        if (schedule.getId() == null) {
            schedule.setCreatedDate(LocalDateTime.now());
        }
        schedule.setUpdatedDate(LocalDateTime.now());
        return scheduleRepository.save(schedule);
    }

    @Override
    public void delete(Long id) {
        scheduleRepository.deleteById(id);
    }

    @Override
    public boolean exists(Long id) {
        return scheduleRepository.existsById(id);
    }

    // Specific business logic methods only
    public List<Schedule> getSchedulesByRouteId(Long routeId) {
        return scheduleRepository.findByRouteId(routeId);
    }
    
    public List<Schedule> getSchedulesByStatus(String status) {
        return scheduleRepository.findByStatus(status);
    }
    
    public List<Schedule> getSchedulesByDriverId(Long driverId) {
        return scheduleRepository.findByDriverId(driverId);
    }
    
    public List<Schedule> getSchedulesByTicketingOfficerId(Long ticketingOfficerId) {
        return scheduleRepository.findByTicketingOfficerId(ticketingOfficerId);
    }
}