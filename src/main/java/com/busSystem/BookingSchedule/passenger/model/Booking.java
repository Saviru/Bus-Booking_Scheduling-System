package com.busSystem.BookingSchedule.passenger.model;

import com.busSystem.BookingSchedule.user.Model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking extends Model {

    @Column(name = "passenger_id")
    private Long passengerId;
    
    @Column(name = "route_id")
    private Long routeId;
    
    @Column(name = "schedule_id")
    private Long scheduleId;
    
    @Column(name = "bus_id")
    private Long busId;

    @Column(name = "ticket_type")
    private String ticketType;
    
    @Column(name = "fare_amount")
    private Double fareAmount;
    
    @Column(name = "discount_applied")
    private Double discountApplied;
    
    @Column(name = "final_amount")
    private Double finalAmount;
    
    @Column(name = "booking_date")
    private LocalDateTime bookingDate;
    
    @Column(name = "travel_date")
    private LocalDateTime travelDate;
    
    @Column(name = "seat_number")
    private String seatNumber;
    
    @Column(name = "special_requests")
    private String specialRequests;
    
    @Column(name = "status")
    private String status;

    // Constructors
    public Booking() {}
    
    public Booking(Long passengerId, Long routeId, Long scheduleId, Long busId, String ticketType,
                  Double fareAmount, Double discountApplied, Double finalAmount,
                  LocalDateTime travelDate, String seatNumber, String status) {
        this.passengerId = passengerId;
        this.routeId = routeId;
        this.scheduleId = scheduleId;
        this.busId = busId;
        this.ticketType = ticketType;
        this.fareAmount = fareAmount;
        this.discountApplied = discountApplied;
        this.finalAmount = finalAmount;
        this.travelDate = travelDate;
        this.seatNumber = seatNumber;
        this.status = status;
        this.bookingDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getPassengerId() { return passengerId; }
    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }
    
    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }
    
    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
    
    public Long getBusId() { return busId; }
    public void setBusId(Long busId) { this.busId = busId; }

    public String getTicketType() { return ticketType; }
    public void setTicketType(String ticketType) { this.ticketType = ticketType; }
    
    public Double getFareAmount() { return fareAmount; }
    public void setFareAmount(Double fareAmount) { this.fareAmount = fareAmount; }
    
    public Double getDiscountApplied() { return discountApplied; }
    public void setDiscountApplied(Double discountApplied) { this.discountApplied = discountApplied; }
    
    public Double getFinalAmount() { return finalAmount; }
    public void setFinalAmount(Double finalAmount) { this.finalAmount = finalAmount; }
    
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    
    public LocalDateTime getTravelDate() { return travelDate; }
    public void setTravelDate(LocalDateTime travelDate) { this.travelDate = travelDate; }
    
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    
    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}