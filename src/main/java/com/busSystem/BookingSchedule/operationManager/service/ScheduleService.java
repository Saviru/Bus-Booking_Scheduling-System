package com.busSystem.BookingSchedule.operationManager.service;

import com.busSystem.BookingSchedule.operationManager.model.Schedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {

    List<Schedule> getAllSchedules();
    
    Optional<Schedule> getScheduleById(Long id);
    
    Schedule saveSchedule(Schedule schedule);
    
    void deleteSchedule(Long id);
    
    List<Schedule> getSchedulesByStatus(String status);
    
    List<Schedule> getSchedulesByDate(LocalDate date);
    
    List<Schedule> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate);
    
    List<Schedule> getSchedulesByRouteId(Long routeId);
    
    boolean isScheduleIdExists(String scheduleId);
}