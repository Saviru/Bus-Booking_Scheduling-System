package com.busSystem.BookingSchedule.operationsManager.repository;

import com.busSystem.BookingSchedule.operationsManager.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByRouteId(Long routeId);
    
    List<Schedule> findByStatus(String status);
    
    List<Schedule> findByDriverId(Long driverId);
    
    List<Schedule> findByTicketingOfficerId(Long ticketingOfficerId);
    
    @Query("SELECT s FROM Schedule s WHERE s.route.id = :routeId AND s.status = :status")
    List<Schedule> findByRouteIdAndStatus(@Param("routeId") Long routeId, @Param("status") String status);
}