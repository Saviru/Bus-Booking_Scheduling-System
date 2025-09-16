package com.busSystem.BookingSchedule.service;

import com.busSystem.BookingSchedule.model.Booking;
import com.busSystem.BookingSchedule.model.Passenger;
import com.busSystem.BookingSchedule.model.Schedule;
import com.busSystem.BookingSchedule.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private ScheduleService scheduleService;
    
    // Create booking
    public Booking createBooking(Passenger passenger, Schedule schedule, LocalDate travelDate,
                               Integer numberOfSeats, String seatPreference, String paymentMethod,
                               String specialRequests) {
        
        // Validate travel date
        if (travelDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Travel date cannot be in the past");
        }
        
        // Check seat availability
        if (!scheduleService.hasAvailableSeats(schedule, numberOfSeats)) {
            throw new IllegalArgumentException("Not enough seats available");
        }
        
        // Calculate total amount
        BigDecimal totalAmount = schedule.getRoute().getBaseFare()
                                       .multiply(BigDecimal.valueOf(numberOfSeats));
        
        // Create booking
        Booking booking = new Booking(passenger, schedule, travelDate, 
                                    numberOfSeats, totalAmount, paymentMethod);
        booking.setSeatPreference(seatPreference);
        booking.setSpecialRequests(specialRequests);
        
        // Save booking
        Booking savedBooking = bookingRepository.save(booking);
        
        // Update available seats
        scheduleService.updateAvailableSeats(schedule, numberOfSeats);
        
        return savedBooking;
    }
    
    // Read operations
    public List<Booking> getBookingsByPassenger(Passenger passenger) {
        return bookingRepository.findByPassengerOrderByBookingDateDesc(passenger);
    }
    
    public List<Booking> getUpcomingBookings(Passenger passenger) {
        return bookingRepository.findUpcomingBookingsByPassenger(passenger);
    }
    
    public List<Booking> getPastBookings(Passenger passenger) {
        return bookingRepository.findPastBookingsByPassenger(passenger);
    }
    
    public List<Booking> getBookingsByStatus(Passenger passenger, Booking.BookingStatus status) {
        return bookingRepository.findByPassengerAndStatus(passenger, status);
    }
    
    public List<Booking> getActiveBookings(Passenger passenger) {
        List<Booking.BookingStatus> activeStatuses = Arrays.asList(
            Booking.BookingStatus.PENDING, 
            Booking.BookingStatus.CONFIRMED
        );
        return bookingRepository.findByPassengerAndStatusIn(passenger, activeStatuses);
    }
    
    public List<Booking> getBookingsByDateRange(Passenger passenger, LocalDate fromDate, LocalDate toDate) {
        return bookingRepository.findByPassengerAndTravelDateBetween(passenger, fromDate, toDate);
    }
    
    public Optional<Booking> findByIdAndPassenger(Long bookingId, Passenger passenger) {
        return bookingRepository.findByIdAndPassengerId(bookingId, passenger.getId());
    }
    
    public Optional<Booking> findByBookingReference(String bookingReference) {
        return bookingRepository.findByBookingReference(bookingReference);
    }
    
    // Update operations
    public boolean updateBooking(Long bookingId, Passenger passenger, LocalDate newTravelDate,
                               String newSeatPreference, String newSpecialRequests) {
        
        Optional<Booking> optionalBooking = findByIdAndPassenger(bookingId, passenger);
        if (optionalBooking.isEmpty()) {
            return false;
        }
        
        Booking booking = optionalBooking.get();
        
        // Check if booking can be modified
        if (!booking.canBeModified()) {
            throw new IllegalStateException("Booking cannot be modified");
        }
        
        // Validate new travel date
        if (newTravelDate.isBefore(LocalDate.now().plusDays(1))) {
            throw new IllegalArgumentException("Travel date must be at least 1 day in advance");
        }
        
        // Update booking details
        booking.setTravelDate(newTravelDate);
        booking.setSeatPreference(newSeatPreference);
        booking.setSpecialRequests(newSpecialRequests);
        
        bookingRepository.save(booking);
        return true;
    }
    
    public boolean confirmBooking(Long bookingId, Passenger passenger, String paymentReference) {
        Optional<Booking> optionalBooking = findByIdAndPassenger(bookingId, passenger);
        if (optionalBooking.isEmpty()) {
            return false;
        }
        
        Booking booking = optionalBooking.get();
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            return false;
        }
        
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setPaymentReference(paymentReference);
        
        bookingRepository.save(booking);
        return true;
    }
    
    // Cancel booking
    public boolean cancelBooking(Long bookingId, Passenger passenger, String reason) {
        Optional<Booking> optionalBooking = findByIdAndPassenger(bookingId, passenger);
        if (optionalBooking.isEmpty()) {
            return false;
        }
        
        Booking booking = optionalBooking.get();
        
        // Check if booking can be cancelled
        if (!booking.canBeCancelled()) {
            throw new IllegalStateException("Booking cannot be cancelled");
        }
        
        // Calculate refund amount
        BigDecimal refundAmount = booking.calculateRefundAmount();
        
        // Update booking status
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setCancellationReason(reason);
        booking.setRefundAmount(refundAmount);
        
        if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            booking.setRefundProcessedAt(LocalDateTime.now());
        }
        
        // Release seats back to schedule
        scheduleService.releaseSeats(booking.getSchedule(), booking.getNumberOfSeats());
        
        bookingRepository.save(booking);
        return true;
    }
    
    // Delete booking (hard delete - admin only)
    public boolean deleteBooking(Long bookingId, Passenger passenger) {
        Optional<Booking> optionalBooking = findByIdAndPassenger(bookingId, passenger);
        if (optionalBooking.isEmpty()) {
            return false;
        }
        
        Booking booking = optionalBooking.get();
        
        // Only allow deletion of cancelled bookings
        if (booking.getStatus() != Booking.BookingStatus.CANCELLED) {
            throw new IllegalStateException("Only cancelled bookings can be deleted");
        }
        
        bookingRepository.delete(booking);
        return true;
    }
    
    // Utility methods
    public Long getTotalBookingsCount(Passenger passenger) {
        return bookingRepository.countBookingsByPassenger(passenger);
    }
    
    public BigDecimal calculateBookingAmount(Schedule schedule, Integer numberOfSeats) {
        return schedule.getRoute().getBaseFare().multiply(BigDecimal.valueOf(numberOfSeats));
    }
    
    public boolean isBookingReferenceUnique(String bookingReference) {
        return !bookingRepository.existsByBookingReference(bookingReference);
    }
    
    // Check seat availability for specific date
    public boolean isSeatsAvailable(Schedule schedule, LocalDate travelDate, Integer requiredSeats) {
        Long bookedSeats = bookingRepository.countBookedSeatsByScheduleAndDate(schedule, travelDate);
        if (bookedSeats == null) bookedSeats = 0L;
        
        long availableSeats = schedule.getTotalSeats() - bookedSeats;
        return availableSeats >= requiredSeats;
    }
}