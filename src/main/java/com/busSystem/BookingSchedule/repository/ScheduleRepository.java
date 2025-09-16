package com.busSystem.BookingSchedule.repository;

import com.busSystem.BookingSchedule.model.Schedule;
import com.busSystem.BookingSchedule.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    
    List<Schedule> findByRouteAndStatus(Route route, Schedule.ScheduleStatus status);
    
    List<Schedule> findByStatus(Schedule.ScheduleStatus status);
    
    @Query("SELECT s FROM Schedule s WHERE s.route = :route AND s.status = 'ACTIVE' ORDER BY s.departureTime")
    List<Schedule> findActiveSchedulesByRoute(@Param("route") Route route);
    
    @Query("SELECT s FROM Schedule s WHERE s.route.id = :routeId AND s.status = 'ACTIVE' AND s.availableSeats > 0 ORDER BY s.departureTime")
    List<Schedule> findAvailableSchedulesByRouteId(@Param("routeId") Long routeId);
    
    @Query("SELECT s FROM Schedule s WHERE s.availableSeats > :minSeats AND s.status = 'ACTIVE'")
    List<Schedule> findSchedulesWithAvailableSeats(@Param("minSeats") Integer minSeats);
    
    @Query("SELECT s FROM Schedule s WHERE s.route.origin = :origin AND s.route.destination = :destination AND s.status = 'ACTIVE' AND s.availableSeats > 0 ORDER BY s.departureTime")
    List<Schedule> findAvailableSchedulesByRoute(@Param("origin") String origin, 
                                               @Param("destination") String destination);
    
    boolean existsByRouteAndDepartureTimeAndArrivalTime(Route route, LocalTime departureTime, LocalTime arrivalTime);
}