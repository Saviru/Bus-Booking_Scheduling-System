package com.busSystem.BookingSchedule.service;

import com.busSystem.BookingSchedule.model.Schedule;
import com.busSystem.BookingSchedule.model.Route;
import com.busSystem.BookingSchedule.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ScheduleService {
    
    @Autowired
    private ScheduleRepository scheduleRepository;
    
    public List<Schedule> getAvailableSchedulesByRoute(Route route) {
        return scheduleRepository.findActiveSchedulesByRoute(route);
    }
    
    public List<Schedule> getAvailableSchedulesByRouteId(Long routeId) {
        return scheduleRepository.findAvailableSchedulesByRouteId(routeId);
    }
    
    public List<Schedule> getAvailableSchedules(String origin, String destination) {
        return scheduleRepository.findAvailableSchedulesByRoute(origin, destination);
    }
    
    public Optional<Schedule> findById(Long id) {
        return scheduleRepository.findById(id);
    }
    
    public List<Schedule> getSchedulesWithAvailableSeats(Integer minSeats) {
        return scheduleRepository.findSchedulesWithAvailableSeats(minSeats);
    }
    
    public boolean hasAvailableSeats(Schedule schedule, Integer requiredSeats) {
        return schedule.getAvailableSeats() != null && 
               schedule.getAvailableSeats() >= requiredSeats;
    }
    
    public void updateAvailableSeats(Schedule schedule, Integer seatsToReduce) {
        if (schedule.getAvailableSeats() != null) {
            schedule.setAvailableSeats(schedule.getAvailableSeats() - seatsToReduce);
            scheduleRepository.save(schedule);
        }
    }
    
    public void releaseSeats(Schedule schedule, Integer seatsToRelease) {
        if (schedule.getAvailableSeats() != null) {
            schedule.setAvailableSeats(schedule.getAvailableSeats() + seatsToRelease);
            scheduleRepository.save(schedule);
        }
    }
}