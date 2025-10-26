package com.busSystem.BookingSchedule.ticketingOfficer.repository;

import com.busSystem.BookingSchedule.ticketingOfficer.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    List<Ticket> findByPassengerId(Long passengerId);
    List<Ticket> findByRouteId(Long routeId);
    List<Ticket> findByTicketType(String ticketType);
    List<Ticket> findByStatus(String status);
    
    @Query("SELECT t FROM Booking t WHERE t.travelDate BETWEEN :startDate AND :endDate")
    List<Ticket> findByTravelDateBetween(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Booking t WHERE t.bookingDate BETWEEN :startDate AND :endDate")
    List<Ticket> findByBookingDateBetween(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
}