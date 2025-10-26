package com.busSystem.BookingSchedule.passenger.service;

import com.busSystem.BookingSchedule.passenger.model.Booking;
import com.busSystem.BookingSchedule.passenger.repository.BookingRepository;
import com.busSystem.BookingSchedule.user.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class BookingService extends Service<Booking, Long> {

    @Autowired
    private BookingRepository bookingRepository;

    // Implement abstract methods from base Service class
    @Override
    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    @Override
    public Optional<Booking> getById(Long id) {
        return bookingRepository.findById(id);
    }

    @Override
    public Booking save(Booking booking) {
        booking.setUpdatedDate(LocalDateTime.now());
        if (booking.getId() == null) {
            booking.setCreatedDate(LocalDateTime.now());
            booking.setBookingDate(LocalDateTime.now());
        }
        return bookingRepository.save(booking);
    }

    @Override
    public void delete(Long id) {
        bookingRepository.deleteById(id);
    }

    @Override
    public boolean exists(Long id) {
        return bookingRepository.existsById(id);
    }

    // Specific business logic methods only
    public List<Booking> getBookingsByPassenger(Long passengerId) {
        return bookingRepository.findByPassengerIdOrderByCreatedDateDesc(passengerId);
    }
    
    public List<Booking> getBookingsByPassengerAndStatus(Long passengerId, String status) {
        return bookingRepository.findByPassengerIdAndStatus(passengerId, status);
    }
    
    public List<Booking> getBookingsByDateRange(Long passengerId, LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.findByPassengerIdAndTravelDateBetween(passengerId, startDate, endDate);
    }

    public List<Booking> getBookingsByScheduleAndDate(Long scheduleId, LocalDateTime travelDate) {
        return bookingRepository.findByScheduleIdAndTravelDate(scheduleId, travelDate);
    }

    public boolean isSeatAvailable(Long scheduleId, LocalDateTime travelDate, String seatNumber) {
        List<Booking> existingBookings = bookingRepository.findByScheduleIdAndTravelDateAndSeatNumber(
            scheduleId, travelDate, seatNumber);
        return existingBookings.isEmpty();
    }

    public List<String> getBookedSeats(Long scheduleId, LocalDateTime travelDate) {
        List<Booking> bookings = bookingRepository.findByScheduleIdAndTravelDate(scheduleId, travelDate);
        return bookings.stream()
            .filter(b -> !"CANCELLED".equals(b.getStatus()))
            .map(Booking::getSeatNumber)
            .toList();
    }

    public List<Booking> getBookingsByBus(Long busId) {
        return bookingRepository.findByBusId(busId);
    }
}