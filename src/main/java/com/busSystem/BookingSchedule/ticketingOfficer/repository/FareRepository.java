package com.busSystem.BookingSchedule.ticketingOfficer.repository;

import com.busSystem.BookingSchedule.ticketingOfficer.model.Fare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FareRepository extends JpaRepository<Fare, Long> {
    
    List<Fare> findByRouteId(Long routeId);
    List<Fare> findByTicketType(String ticketType);
    List<Fare> findByStatus(String status);
    
    @Query("SELECT f FROM Fare f WHERE f.routeId = :routeId AND f.ticketType = :ticketType AND f.status = 'ACTIVE'")
    List<Fare> findActiveByRouteIdAndTicketType(@Param("routeId") Long routeId, 
                                              @Param("ticketType") String ticketType);
    
    @Query("SELECT f FROM Fare f WHERE :currentDate BETWEEN f.validFrom AND f.validTo AND f.status = 'ACTIVE'")
    List<Fare> findValidFares(@Param("currentDate") LocalDateTime currentDate);
}