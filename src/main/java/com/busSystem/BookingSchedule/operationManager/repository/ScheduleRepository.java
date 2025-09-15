package com.busSystem.BookingSchedule.operationManager.repository;

import com.busSystem.BookingSchedule.operationManager.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByStatus(String status);
    
    List<Schedule> findByScheduleDate(LocalDate date);
    
    @Query("SELECT s FROM Schedule s WHERE s.scheduleDate BETWEEN :startDate AND :endDate")
    List<Schedule> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    List<Schedule> findByRouteId(Long routeId);
    
    boolean existsByScheduleId(String scheduleId);
}