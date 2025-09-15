package com.busSystem.BookingSchedule.operationManager.service;

import com.busSystem.BookingSchedule.operationManager.model.Schedule;
import com.busSystem.BookingSchedule.operationManager.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    
    @Autowired
    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }
    
    @Override
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }
    
    @Override
    public Optional<Schedule> getScheduleById(Long id) {
        return scheduleRepository.findById(id);
    }
    
    @Override
    public Schedule saveSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }
    
    @Override
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }
    
    @Override
    public List<Schedule> getSchedulesByStatus(String status) {
        return scheduleRepository.findByStatus(status);
    }
    
    @Override
    public List<Schedule> getSchedulesByDate(LocalDate date) {
        return scheduleRepository.findByScheduleDate(date);
    }
    
    @Override
    public List<Schedule> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        return scheduleRepository.findByDateRange(startDate, endDate);
    }
    
    @Override
    public List<Schedule> getSchedulesByRouteId(Long routeId) {
        return scheduleRepository.findByRouteId(routeId);
    }
    
    @Override
    public boolean isScheduleIdExists(String scheduleId) {
        return scheduleRepository.existsByScheduleId(scheduleId);
    }
}