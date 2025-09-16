package com.busSystem.BookingSchedule.repository;

import com.busSystem.BookingSchedule.model.Booking;
import com.busSystem.BookingSchedule.model.Passenger;
import com.busSystem.BookingSchedule.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    Optional<Booking> findByBookingReference(String bookingReference);
    
    List<Booking> findByPassenger(Passenger passenger);
    
    List<Booking> findByPassengerOrderByBookingDateDesc(Passenger passenger);
    
    List<Booking> findByPassengerAndStatus(Passenger passenger, Booking.BookingStatus status);
    
    List<Booking> findBySchedule(Schedule schedule);
    
    List<Booking> findByTravelDate(LocalDate travelDate);
    
    @Query("SELECT b FROM Booking b WHERE b.passenger = :passenger AND b.travelDate >= :fromDate AND b.travelDate <= :toDate ORDER BY b.travelDate")
    List<Booking> findByPassengerAndTravelDateBetween(@Param("passenger") Passenger passenger,
                                                     @Param("fromDate") LocalDate fromDate,
                                                     @Param("toDate") LocalDate toDate);
    
    @Query("SELECT b FROM Booking b WHERE b.passenger = :passenger AND b.status IN :statuses ORDER BY b.bookingDate DESC")
    List<Booking> findByPassengerAndStatusIn(@Param("passenger") Passenger passenger,
                                           @Param("statuses") List<Booking.BookingStatus> statuses);
    
    @Query("SELECT b FROM Booking b WHERE b.passenger = :passenger AND b.travelDate >= CURRENT_DATE ORDER BY b.travelDate")
    List<Booking> findUpcomingBookingsByPassenger(@Param("passenger") Passenger passenger);
    
    @Query("SELECT b FROM Booking b WHERE b.passenger = :passenger AND b.travelDate < CURRENT_DATE ORDER BY b.travelDate DESC")
    List<Booking> findPastBookingsByPassenger(@Param("passenger") Passenger passenger);
    
    @Query("SELECT b FROM Booking b WHERE b.passenger = :passenger AND b.status = :status AND b.travelDate >= CURRENT_DATE ORDER BY b.travelDate")
    List<Booking> findUpcomingBookingsByPassengerAndStatus(@Param("passenger") Passenger passenger,
                                                          @Param("status") Booking.BookingStatus status);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.schedule = :schedule AND b.travelDate = :travelDate AND b.status IN ('PENDING', 'CONFIRMED')")
    Long countActiveBookingsByScheduleAndDate(@Param("schedule") Schedule schedule, 
                                            @Param("travelDate") LocalDate travelDate);
    
    @Query("SELECT SUM(b.numberOfSeats) FROM Booking b WHERE b.schedule = :schedule AND b.travelDate = :travelDate AND b.status IN ('PENDING', 'CONFIRMED')")
    Long countBookedSeatsByScheduleAndDate(@Param("schedule") Schedule schedule, 
                                         @Param("travelDate") LocalDate travelDate);
    
    @Query("SELECT b FROM Booking b WHERE b.bookingDate >= :fromDate AND b.bookingDate <= :toDate ORDER BY b.bookingDate DESC")
    List<Booking> findBookingsByDateRange(@Param("fromDate") LocalDateTime fromDate,
                                        @Param("toDate") LocalDateTime toDate);
    
    @Query("SELECT b FROM Booking b WHERE b.passenger.id = :passengerId AND b.id = :bookingId")
    Optional<Booking> findByIdAndPassengerId(@Param("bookingId") Long bookingId, 
                                           @Param("passengerId") Long passengerId);
    
    boolean existsByBookingReference(String bookingReference);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.passenger = :passenger")
    Long countBookingsByPassenger(@Param("passenger") Passenger passenger);
}