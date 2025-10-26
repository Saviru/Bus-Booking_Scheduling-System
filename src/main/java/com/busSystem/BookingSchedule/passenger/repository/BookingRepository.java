package com.busSystem.BookingSchedule.passenger.repository;

import com.busSystem.BookingSchedule.passenger.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByPassengerId(Long passengerId);
    List<Booking> findByRouteId(Long routeId);
    List<Booking> findByStatus(String status);
    List<Booking> findByPassengerIdAndStatus(Long passengerId, String status);
    List<Booking> findByTravelDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Booking> findByPassengerIdOrderByCreatedDateDesc(Long passengerId);
    
    @Query("SELECT b FROM Booking b WHERE b.passengerId = :passengerId AND b.travelDate >= :startDate AND b.travelDate <= :endDate")
    List<Booking> findByPassengerIdAndTravelDateBetween(@Param("passengerId") Long passengerId, 
                                                        @Param("startDate") LocalDateTime startDate, 
                                                        @Param("endDate") LocalDateTime endDate);

    List<Booking> findByScheduleIdAndTravelDate(Long scheduleId, LocalDateTime travelDate);

    @Query("SELECT b FROM Booking b WHERE b.scheduleId = :scheduleId AND b.travelDate = :travelDate AND b.seatNumber = :seatNumber AND b.status != 'CANCELLED'")
    List<Booking> findByScheduleIdAndTravelDateAndSeatNumber(@Param("scheduleId") Long scheduleId,
                                                              @Param("travelDate") LocalDateTime travelDate,
                                                              @Param("seatNumber") String seatNumber);

    List<Booking> findByBusId(Long busId);
}